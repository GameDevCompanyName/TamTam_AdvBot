package me.evgen.advbot

import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.communications.LongPollingStartingParams
import chat.tamtam.botsdk.communications.longPolling
import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.model.request.SendMessage
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
            "Здарова бандиты" sendFor it.chatId
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

//            onCommand("/test") {
//                "PASSWORD REQUIRED" sendFor it.command.message.sender.userId
//                BotController.statesMap[it.command.message.sender] = States.TEST
//
//            }
//
//            onCommand("/close") {
//                if (BotController.statesMap[it.command.message.sender] == States.ADMIN) {
//                    BotController.statesMap[it.command.message.sender] = States.NORMAL
//                    "Admin mode closed" sendFor it.command.message.sender.userId
//                } else {
//                    """I'm sorry, but I don't know this command, you can try /start
//                    |if you don't remember all my available command.""".trimMargin() sendFor it.command.message.sender.userId
//                }
//            }
//
//            onCommand("/maps") {
//                for (entry in BotController.tempAdMap) {
//                    val (user, ad) = entry
//                    """UserId: ${user.userId}
//                            |UserName: ${user.name}
//                            |Ad name: ${ad.title}
//                            |Ad text: ${ad.text}
//                        """.trimMargin() sendFor it.command.message.sender.userId
//                }
//            }

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
//            answerOnCallback(Payloads.TEST) {
//                "${it.message?.body?.attachments?.get(0)}" replaceCurrentMessage it.callback.callbackId
//            }
        }

        messages {
            answerOnMessage { messageState ->
                val currentState = BotController.getCurrentState(messageState.message.sender)
                if (currentState == null || currentState !is MessageListener) { //TODO fix
                    val result = SendMessage("Для начала работы введите команду /start") sendFor messageState.message.recipient.chatId
                    when (result) {
                        is ResultRequest.Success -> result.response
                        is ResultRequest.Failure -> result.exception
                    }
                    return@answerOnMessage
                }
                currentState.onMessageReceived(messageState, requests)

//                    States.TEST -> {
//                        if (messageState.message.body.text == "admin") {
//                            BotController.statesMap[messageState.message.sender] = States.ADMIN
//                            "Welcome, master" sendFor messageState.message.sender.userId
//                        } else BotController.statesMap[messageState.message.sender] = States.NORMAL
//                    }
//                }
            }

        }

    }
}
