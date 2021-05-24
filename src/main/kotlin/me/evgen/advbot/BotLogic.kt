package me.evgen.advbot

import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.communications.LongPollingStartingParams
import chat.tamtam.botsdk.communications.longPolling
import chat.tamtam.botsdk.model.prepared.AttachmentPhoto
import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.model.request.SendMessage
import chat.tamtam.botsdk.model.response.ChatType
import com.google.gson.Gson
import me.evgen.advbot.db.DBSessionFactoryUtil
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.model.state.MessageListener
import me.evgen.advbot.model.state.StartState
import me.evgen.advbot.model.state.WelcomeState
import me.evgen.advbot.service.UserService
import java.lang.Exception

fun main() {
    longPolling(LongPollingStartingParams("Z0C8HWGP311wCZEDRtDJtFhxHVI0C0IXnd-pcEDmDMQ")) {

        onStartBot {
            WelcomeState(System.currentTimeMillis()).handle(it, requests)
        }

        onAddBotToChat {
            when (val res = requests.getChat(it.chatId)) {
                is ResultRequest.Success ->  {
                    val chatName = res.response.title
                    "Вы успешно добавили бота в $chatName" sendFor it.user.userId
                    DBSessionFactoryUtil.localStorage.addChat(it.getUserId(), res.response)
                }
                is ResultRequest.Failure -> res.exception
            }
        }

        onRemoveBotFromChat {
            //TODO: оповещение об удалении бота из чата и удаление его из хранилища
        }

        commands {

            onCommand("/start") {
                val newState = StartState(System.currentTimeMillis())
                BotController.moveTo(newState, it.getUserId().id, isForce = true) { oldState ->
                    newState.handle(it, oldState, requests)
                }
            }

            onUnknownCommand {
                """I'm sorry, but I don't know this command, you can try /start
                    |if you don't remember all my available command.""".trimMargin() sendFor it.command.message.sender.userId
            }

        }

        callbacks {

            defaultAnswer {
                val gson = Gson()

                try {
                    val payload = gson.fromJson(it.callback.payload, Payload::class.java)
                    val newState = gson.fromJson(payload.jsonState, Class.forName(payload.className)) as BaseState

                    BotController.moveTo(newState, it.getUserId().id) { oldState ->
                        if (newState is CustomCallbackState) {
                            newState.handle(it, oldState, requests)
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                    //TODO norm log
                }
            }
            answerOnCallback(Payloads.WIP) {
                "Work in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
        }

        messages {
            answerOnMessage { messageState ->
                val currentState = UserService.getCurrentState(messageState.getUserId().id)
                if ((currentState == null || currentState !is MessageListener)) { //TODO fix
                    when (messageState.message.recipient.chatType) {
                        ChatType.DIALOG -> {
                            when (val result =
                                SendMessage("Для начала работы введите команду /start") sendFor messageState.message.recipient.chatId) {
                                is ResultRequest.Success -> result.response
                                is ResultRequest.Failure -> result.exception
                            }
                            return@answerOnMessage
                        }
                        ChatType.CHANNEL -> {
//                            when (val result =
//                                SendMessage(
//                                    """${messageState.message.body.attachments[0]}
//                                    """.trimMargin()
//                                ) sendFor messageState.message.recipient.chatId) {
//                                is ResultRequest.Success -> result.response
//                                is ResultRequest.Failure -> result.exception
//                            }
                            val attaches = messageState.message.body.attachments
                            if (attaches.isNotEmpty()) {
                                when(val attach = attaches[0]) {
                                    is AttachmentPhoto -> attach.payload.url sendFor messageState.message.recipient.chatId
                                }
                            }

                            return@answerOnMessage
                        }
                        else -> {
                            return@answerOnMessage
                        }
                    }

                }
                currentState.onMessageReceived(messageState, requests)
            }
        }
    }
}
