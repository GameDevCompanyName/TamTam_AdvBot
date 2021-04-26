import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.communications.LongPollingStartingParams
import chat.tamtam.botsdk.communications.longPolling
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.prepared.Message
import chat.tamtam.botsdk.model.prepared.User
import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.model.request.ReusableMediaParams
import chat.tamtam.botsdk.model.request.UploadType
import chat.tamtam.botsdk.scopes.CallbacksScope
import chat.tamtam.botsdk.scopes.CommandsScope
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.CommandState
import chat.tamtam.botsdk.model.request.SendMessage as RequestSendMessage
import model.*

fun main() {

    val statesMap = mutableMapOf<User, States>()
    val adsMap = mutableMapOf<User, MutableSet<Advert>>()
    val tempAdMap =
        mutableMapOf<User, Advert>() // на этапе создания реклама лежит в этой мапе, после нажатия на "готово" улетает в adsMap

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

        }

        commands {

            onCommand("/start") {
                adsMap[it.command.message.sender] = mutableSetOf()
                statesMap[it.command.message.sender] = States.NORMAL
                val inlineKeyboard = createStartKeyboard()
                // send text for user
                "Вы можете разместить рекламу или предоставить площадку для ее размещения" sendFor it.command.message.sender.userId

                // first prepare text and userId then send for user prepared text with InlineKeyboard or other Attach
                "Выберите один из предложенных вариантов:" prepareFor it.command.message.sender.userId sendWith inlineKeyboard

                //simple request first 5 messages in chat
                // you can check result of your request
//                when (val resultRequest = 5 messagesIn it.command.message.recipient.chatId) {
//                    is ResultRequest.Success -> resultRequest.response.size
//                    is ResultRequest.Failure -> resultRequest.error
//                }

                // You can create extension function if you don't want to leave code here, but you need know,
                // that all extension functions for Scopes, need be 'suspend'.
//                sendTextWithKeyboard(it, inlineKeyboard)
            }

            onCommand("/test") {
                "PASSWORD REQUIRED" sendFor it.command.message.sender.userId
                statesMap[it.command.message.sender] = States.TEST

            }

            onCommand("/close") {
                if (statesMap[it.command.message.sender] == States.ADMIN) {
                    statesMap[it.command.message.sender] = States.NORMAL
                    "Admin mode closed" sendFor it.command.message.sender.userId
                } else {
                    """I'm sorry, but I don't know this command, you can try /start
                    |if you don't remember all my available command.""".trimMargin() sendFor it.command.message.sender.userId
                }
            }

            onCommand("/maps") {
//                if (statesMap[it.command.message.sender] == States.ADMIN) {
//                for (entry in statesMap) {
//                    val (user, state) = entry
//                    """UserId: ${user.userId}
//                            |UserName: ${user.name}
//                            |State: ${state.name}
//                        """.trimMargin() sendFor it.command.message.sender.userId
//                }
                for (entry in tempAdMap) {
                    val (user, ad) = entry
                    """UserId: ${user.userId}
                            |UserName: ${user.name}
                            |Ad name: ${ad.name}
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

            answerOnCallback(Payloads.ADVERT) {
                statesMap[it.callback.user] = States.NORMAL
                val inlineKeyboard = createAdvertKeyboard()
                "Размещение рекламы" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback(Payloads.PLATFORM) {

//                // send message with upload Photo which replace old message
//                "Предоставление площадки" prepareReplacementCurrentMessage
//                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith
//                        UploadParams("res/busy_dog.jpg", UploadType.PHOTO)

                // send message which replace old message
                ("section is under development") answerFor (it.callback.callbackId)

                // send notification (as Toast) for User
                "Work in progress" answerNotification AnswerParams(
                    it.callback.callbackId,
                    it.callback.user.userId
                )
            }

            answerOnCallback(Payloads.CONSTRUCT) {
                val inlineKeyboard = createConstructorKeyboard()
                """Добро пожаловать в конструктор рекламы!
                |Для навигации используйте кнопки:
                 """.trimMargin() prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback(Payloads.BACK_TO_START) {
                val inlineKeyboard = createStartKeyboard()
                "Выберите один из предложенных вариантов:" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback(Payloads.ADV_LIST) {
                val ads = adsMap[it.callback.user]
                val inlineKeyboard = keyboard {
                    if (ads != null) {
                        for (entry in ads) {
                            +buttonRow {
                                +Button(
                                    ButtonType.CALLBACK,
                                    entry.name,
                                    payload = Payloads.WIP
                                )
                            }
                        }
                    }
                    +buttonRow {
                        +Button(
                            ButtonType.CALLBACK,
                            "⬅ Назад",
                            payload = Payloads.ADVERT
                        )
                    }
                }
                if (ads != null) "Ваши объявления:" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
                else "Здесь будут отображаться ваши объявления" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback(Payloads.ADV_SETTINGS) {
                "Работа с рекламой" prepareReplacementCurrentMessage
                        AnswerParams(
                            it.callback.callbackId,
                            it.callback.user.userId
                        ) answerWith createAdvSettingsKeyboard()
            }

            answerOnCallback(Payloads.BACK_TO_ADVERT) {
                val inlineKeyboard = createAdvertKeyboard()
                "Размещение рекламы" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback(Payloads.ADV_NAME) {
                statesMap[it.callback.user] = States.AD_NAMING
                """Введите название будущей рекламы.
                    |Название используется для идентификации и в самом объявлении показано не будет.
                """.trimMargin() prepareReplacementCurrentMessage
                        AnswerParams(
                            it.callback.callbackId,
                            it.callback.user.userId
                        ) answerWith constructorCancelKeyboard()
            }
            answerOnCallback(Payloads.ADV_TEXT) {
                statesMap[it.callback.user] = States.AD_TEXTING
                """Введите текст будущей рекламы.
                    |Именно этот текст будет показан в объявлении.
                """.trimMargin() prepareReplacementCurrentMessage
                        AnswerParams(
                            it.callback.callbackId,
                            it.callback.user.userId
                        ) answerWith constructorCancelKeyboard()
            }
            answerOnCallback(Payloads.ADV_IMG) {

                "Work in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
            answerOnCallback(Payloads.ADV_TARGETS) {

                "Work in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }

            answerOnCallback(Payloads.WIP) {

                "Work in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }

            answerOnCallback(Payloads.TEST) {

                "${it.message?.body?.attachments?.get(0)}" replaceCurrentMessage it.callback.callbackId
            }

            answerOnCallback(Payloads.MAKER_DONE) {
                val user = it.callback.user
                tempAdMap[user]?.let { it1 -> adsMap[user]?.add(it1) }
                tempAdMap.remove(user)
                statesMap[user] = States.NORMAL
                "Реклама успешно создана" answerNotification AnswerParams(
                    it.callback.callbackId,
                    it.callback.user.userId
                )
                "Размещение рекламы" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith createAdvertKeyboard()
            }


        }

        messages {

            // if current update is message, but not contains command, code below will start
            answerOnMessage { messageState ->
//                typingOn(messageState.message.recipient.chatId)
                when (statesMap[messageState.message.sender]) {
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
                        statesMap[messageState.message.sender] = States.NORMAL
                        tempAdMap[messageState.message.sender] = Advert(messageState.message.body.text)
                    }
                    States.AD_TEXTING -> {
                        statesMap[messageState.message.sender] = States.NORMAL
                        val ad = tempAdMap[messageState.message.sender]
                        if (ad != null) {
                            ad.text = messageState.message.body.text
                            """Текущее название рекламы:
                            |${ad.name}
                            |Текущий текст рекламы:
                            |${ad.text}
                            |""".trimMargin() prepareFor
                                    messageState.message.sender.userId sendWith createConstructorKeyboard()
                        }

                    }
                    States.TEST -> {
                        if (messageState.message.body.text == "admin") {
                            statesMap[messageState.message.sender] = States.ADMIN
                            "Welcome, master" sendFor messageState.message.sender.userId
                        } else statesMap[messageState.message.sender] = States.NORMAL
                    }
                    else -> {
                        val result =
                            RequestSendMessage("Для начала работы введите команду /start") sendFor messageState.message.recipient.chatId
                        when (result) {
                            is ResultRequest.Success -> result.response
                            is ResultRequest.Failure -> result.exception
                        }
                        statesMap[messageState.message.sender] = States.NORMAL
                    }
                }


//                typingOff(messageState.message.recipient.chatId)
            }

        }

    }
}

enum class States {
    NORMAL,
    TEST,
    ADMIN,
    AD_NAMING,
    AD_TEXTING
}

private fun CallbacksScope.answerOnCallbackText(payload: String, text: String) {
    answerOnCallback(payload) {
        text answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
    }
}

private suspend fun CommandsScope.sendTextWithKeyboard(state: CommandState, keyboard: InlineKeyboard) {
    "Choose you dinner" prepareFor state.command.message.sender.userId sendWith keyboard
}



