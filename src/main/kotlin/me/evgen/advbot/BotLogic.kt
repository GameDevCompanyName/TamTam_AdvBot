package me.evgen.advbot

import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.communications.LongPollingStartingParams
import chat.tamtam.botsdk.communications.longPolling
import chat.tamtam.botsdk.model.prepared.AttachmentPhoto
import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.model.request.SendMessage
import chat.tamtam.botsdk.model.response.ChatType
import com.google.gson.Gson
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.*
import me.evgen.advbot.service.PlatformService
import me.evgen.advbot.service.UserService

import io.javalin.Javalin
import me.evgen.advbot.model.entity.User
import me.evgen.advbot.model.payment.PaymentMapping
import me.evgen.advbot.model.payment.PaymentStatusEvent
import java.beans.PropertyChangeEvent

fun main() {
    longPolling(LongPollingStartingParams("dg370Ox_HsEK3qPYlhpc-2NZqU4yAGmoa9U_B5ImxHs")) {

        Javalin.create().start(8080).apply {
            get(PaymentMapping.PAY_SUCCESS) {
                BotController.paymentSupport.firePropertyChange(
                    PropertyChangeEvent(
                        "BotLogic",
                        PaymentStatusEvent.PROPERTY_NAME,
                        null,
                        PaymentStatusEvent(
                            true,
                            1, //TODO нужно передавать правильный айди юзера
                            requests
                        )
                    )
                )
            }
            get(PaymentMapping.PAY_ERROR) {
                //TODO
            }
        }

        onStartBot {
            WelcomeState(System.currentTimeMillis()).handle(it, requests)
        }

        onAddBotToChat {
            when (val res = requests.getChat(it.chatId)) {
                is ResultRequest.Success ->  {
                    val chatName = res.response.title
                    "Вы успешно добавили бота в $chatName" sendFor it.user.userId
                    PlatformService.addPlatform(it.user.userId.id, res.response.chatId.id)
                }
                is ResultRequest.Failure -> res.exception
            }
        }

        onRemoveBotFromChat {
            //TODO из-за того, что в базе хранится только айди чата, мы тут никак не можем получить его тайтл, потому что getChatFromServer возращает null
            PlatformService.deletePlatform(it.chatId.id)
        }

        commands {

            onCommand("/start") {
                try {
                    val newState = StartState(System.currentTimeMillis())
                    var user = UserService.findUser(it.getUserIdLong())
                    var result: Long? = user?.id
                    if (result == null) {
                        user = User(it.getUserIdLong(), newState)
                        result = UserService.insertUser(user)
                    }
                    if (result != null) {
                        BotController.moveTo(newState, it.getUserId().id, isForce = true) { oldState ->
                            newState.handle(it, requests)
                        }
                    } else {
                        "Возникла ошибка! Обратитесь к разработчикам.".sendFor(it.getUserId())
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    //TODO log
                }
            }

            onUnknownCommand {
                "Неизвестная команда, используйте /start" sendFor it.command.message.sender.userId
            }

        }

        callbacks {

            defaultAnswer {
                val gson = Gson()

                try {
                    val payload = gson.fromJson(it.callback.payload, Payload::class.java)
                    val newState = gson.fromJson(payload.jsonState, Class.forName(payload.className)) as BaseState

                    BotController.moveTo(newState, it.getUserId().id) { _ ->
                        if (newState is CustomCallbackState) {
                            newState.handle(it, requests)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    //TODO norm log
                }
            }
            answerOnCallback(Payloads.WIP) {
                "Work in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
            answerOnCallback(Payloads.EMPTY) {
                //Nothing
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
