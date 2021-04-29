package me.evgen.advbot

import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.communications.LongPollingStartingParams
import chat.tamtam.botsdk.communications.longPolling
import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.scopes.CallbacksScope
import chat.tamtam.botsdk.scopes.CommandsScope
import chat.tamtam.botsdk.state.CommandState
import me.evgen.advbot.callbacks.*
import me.evgen.advbot.model.TempAdvert
import me.evgen.advbot.storage.LocalStorage
import chat.tamtam.botsdk.model.request.SendMessage as RequestSendMessage

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
                BotController.statesMap[it.command.message.sender] = States.NORMAL
                val inlineKeyboard = createStartKeyboard()
                // send text for user
                "Вы можете разместить рекламу или предоставить площадку для ее размещения" sendFor it.command.message.sender.userId

                // first prepare text and userId then send for user prepared text with InlineKeyboard or other Attach
                "Выберите один из предложенных вариантов:" prepareFor it.command.message.sender.userId sendWith inlineKeyboard

                //simple request first 5 messages in chat. You can check result of your request
//                when (val resultRequest = 5 messagesIn it.command.message.recipient.chatId) {
//                    is ResultRequest.Success -> resultRequest.response.size
//                    is ResultRequest.Failure -> resultRequest.error
//                }

                // You can create extension function if you don't want to leave code here, but you need know,
                // that all extension functions for Scopes, need be 'suspend'.
//                me.evgen.bot.sendTextWithKeyboard(it, inlineKeyboard)
            }

            onCommand("/test") {
                "PASSWORD REQUIRED" sendFor it.command.message.sender.userId
                BotController.statesMap[it.command.message.sender] = States.TEST

            }

            onCommand("/close") {
                if (BotController.statesMap[it.command.message.sender] == States.ADMIN) {
                    BotController.statesMap[it.command.message.sender] = States.NORMAL
                    "Admin mode closed" sendFor it.command.message.sender.userId
                } else {
                    """I'm sorry, but I don't know this command, you can try /start
                    |if you don't remember all my available command.""".trimMargin() sendFor it.command.message.sender.userId
                }
            }

            onCommand("/maps") {
//                if (statesMap[it.command.message.sender] == me.evgen.bot.States.ADMIN) {
//                for (entry in statesMap) {
//                    val (user, state) = entry
//                    """UserId: ${user.userId}
//                            |UserName: ${user.name}
//                            |State: ${state.name}
//                        """.trimMargin() sendFor it.command.message.sender.userId
//                }
                for (entry in BotController.tempAdMap) {
                    val (user, ad) = entry
                    """UserId: ${user.userId}
                            |UserName: ${user.name}
                            |Ad name: ${ad.title}
                            |Ad text: ${ad.text}
                        """.trimMargin() sendFor it.command.message.sender.userId
                }


//                } else {
//                    """I'm sorry, but I don't know this command, you can try /start
//                    |if you don't remember all my available command.""".trimMargin() sendFor it.command.message.sender.userId
//                }
            }

            onUnknownCommand {
                // You can reuse some medias in other messages. Reusable token or id or fileId, you will get after send message with media
//                "Reuse had already sent image" prepareFor it.command.message.sender.userId sendWith ReusableMediaParams(
//                    UploadType.PHOTO,
//                    token = "TOKEN"
//                )

                """I'm sorry, but I don't know this command, you can try /start
                    |if you don't remember all my available command.""".trimMargin() sendFor it.command.message.sender.userId
            }

        }

        callbacks {

            defaultAnswer {
                val resultAnswer = "COMING SOON!" replaceCurrentMessage it.callback.callbackId

                when (resultAnswer) {
                    is ResultRequest.Success -> resultAnswer.response
                    is ResultRequest.Failure -> resultAnswer.exception
                }
            }

            answerOnCallback(Payloads.PLATFORM) {
                callbackPlatform(it)
            }

            answerOnCallback(Payloads.BACK_TO_START) {
                val inlineKeyboard = createStartKeyboard()
                "Выберите один из предложенных вариантов:" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback(Payloads.ADVERT) {
                callbackAdvert(it)
            }
            answerOnCallback(Payloads.ADV_LIST) {
                callbackAdvList(it)
            }
            answerOnCallback(Payloads.ADV_TITLE) {
                callbackAdvTitle(it)
            }
            answerOnCallback(Payloads.ADV_TEXT) {
                callbackAdvText(it)
            }
            answerOnCallback(Payloads.ADV_IMG) {

                "Work in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
            answerOnCallback(Payloads.ADV_TARGETS) {

                "Work in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
            answerOnCallback(Payloads.MAKER_DONE) {
                val user = it.callback.user
                BotController.tempAdMap[user]?.apply {
                    LocalStorage.addAdvert(user, this)
                }
                BotController.tempAdMap.remove(user)
                BotController.statesMap[user] = States.NORMAL
                "Реклама успешно создана" answerNotification AnswerParams(
                    it.callback.callbackId,
                    it.callback.user.userId
                )
                "Размещение рекламы" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith createAdvertKeyboard()
            }

            answerOnCallback(Payloads.WIP) {
                "Work in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
            answerOnCallback(Payloads.TEST) {
                "${it.message?.body?.attachments?.get(0)}" replaceCurrentMessage it.callback.callbackId
            }
        }

        messages {
            answerOnMessage { messageState ->
                when (BotController.statesMap[messageState.message.sender]) {
                    States.NORMAL -> {
                        val result =
                            RequestSendMessage("Для начала работы введите команду /start") sendFor messageState.message.recipient.chatId
                        when (result) {
                            is ResultRequest.Success -> result.response
                            is ResultRequest.Failure -> result.exception
                        }
                    }
                    States.AD_NAMING -> {
                        "Текущее название рекламы: \n${messageState.message.body.text}" prepareFor
                                messageState.message.sender.userId sendWith createConstructorKeyboard()
                        BotController.statesMap[messageState.message.sender] = States.NORMAL
                        BotController.tempAdMap[messageState.message.sender] = TempAdvert().apply {
                            title = messageState.message.body.text
                        }
                    }
                    States.AD_TEXTING -> {
                        BotController.statesMap[messageState.message.sender] = States.NORMAL
                        BotController.tempAdMap[messageState.message.sender]?.apply {
                            text = messageState.message.body.text
                            """Текущее название рекламы:
                            |${title}
                            |Текущий текст рекламы:
                            |${text}
                            |""".trimMargin() prepareFor
                                    messageState.message.sender.userId sendWith createConstructorKeyboard()
                        }
                    }
                    States.TEST -> {
                        if (messageState.message.body.text == "admin") {
                            BotController.statesMap[messageState.message.sender] = States.ADMIN
                            "Welcome, master" sendFor messageState.message.sender.userId
                        } else BotController.statesMap[messageState.message.sender] = States.NORMAL
                    }
                    else -> {
                        val result =
                            RequestSendMessage("Для начала работы введите команду /start") sendFor messageState.message.recipient.chatId
                        when (result) {
                            is ResultRequest.Success -> result.response
                            is ResultRequest.Failure -> result.exception
                        }
                        BotController.statesMap[messageState.message.sender] = States.NORMAL
                    }
                }
            }

        }

    }
}

enum class States {
    NORMAL,

    AD_NAMING,
    AD_TEXTING,

    TEST,
    ADMIN
}

private fun CallbacksScope.answerOnCallbackText(payload: String, text: String) {
    answerOnCallback(payload) {
        text answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
    }
}

private suspend fun CommandsScope.sendTextWithKeyboard(state: CommandState, keyboard: InlineKeyboard) {
    "Choose you dinner" prepareFor state.command.message.sender.userId sendWith keyboard
}