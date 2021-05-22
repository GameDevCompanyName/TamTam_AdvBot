package me.evgen.advbot

import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.communications.LongPollingStartingParams
import chat.tamtam.botsdk.communications.longPolling
import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.model.request.SendMessage
import chat.tamtam.botsdk.model.response.ChatType
import com.google.gson.Gson
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.model.state.MessageListener
import me.evgen.advbot.model.state.StartState
import java.lang.Exception

fun main() {
    longPolling(LongPollingStartingParams("Z0C8HWGP311wCZEDRtDJtFhxHVI0C0IXnd-pcEDmDMQ")) {
        // when User start your bot, code below will start
        onStartBot {
            initialText(it.user.name) sendFor it.user.userId
        }

        // when something added your bot to Chat, code below will start
        onAddBotToChat {
            //Nothing
        }

        // when something removed your bot from Chat, code below will start
        onRemoveBotFromChat {
            //Nothing
        }

        commands {

            onCommand("/start") {
                val newState = StartState(System.currentTimeMillis())
                BotController.moveTo(newState, it.command.message.sender, isForce = true) { oldState ->
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

                    BotController.moveTo(newState, it.callback.user) { oldState ->
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
                val currentState = BotController.getCurrentState(messageState.message.sender)
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
                        ChatType.CHANNEL -> { //used for test purpose for now
                            when (val result =
                                SendMessage("Attachments: ${messageState.message.link}") sendFor messageState.message.recipient.chatId) {
                                is ResultRequest.Success -> result.response
                                is ResultRequest.Failure -> result.exception
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
