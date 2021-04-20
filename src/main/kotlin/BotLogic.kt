import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.communications.LongPollingStartingParams
import chat.tamtam.botsdk.communications.longPolling
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.model.request.ReusableMediaParams
import chat.tamtam.botsdk.model.request.UploadType
import chat.tamtam.botsdk.scopes.CommandsScope
import chat.tamtam.botsdk.state.CommandState
import chat.tamtam.botsdk.model.request.SendMessage as RequestSendMessage

fun main() {

    longPolling(LongPollingStartingParams("Z0C8HWGP311wCZEDRtDJtFhxHVI0C0IXnd-pcEDmDMQ")) {

        // when User start your bot, code below will start
        onStartBot {
            """Привет! Я бот для работы с рекламой.
                |Для начала работы введите команду /start
            """.trimMargin() sendFor it.user.userId
        }

        // when something added your bot to Chat, code below will start
        onAddBotToChat {

        }

        // when something removed your bot from Chat, code below will start
        onRemoveBotFromChat {

        }

        commands {

            onCommand("/start") {
                val inlineKeyboard = createStartKeyboard()
                // send text for user
                "Вы можете разместить рекламу или предоставить площадку для ее размещения" sendFor it.command.message.sender.userId

                // first prepare text and userId then send for user prepared text with InlineKeyboard or other Attach
                "Выберите один из предложенных вариантов:" prepareFor it.command.message.sender.userId sendWith inlineKeyboard

                //simple request first 5 messages in chat
                val resultRequest = 5 messagesIn it.command.message.recipient.chatId
                // you can check result of your request
                when (resultRequest) {
                    is ResultRequest.Success -> resultRequest.response.size
                    is ResultRequest.Failure -> resultRequest.error
                }

                // You can create extension function if you don't want to leave code here, but you need know,
                // that all extension functions for Scopes, need be 'suspend'.
//                sendTextWithKeyboard(it, inlineKeyboard)
            }

            onUnknownCommand {
                // You can reuse some medias in other messages. Reusable token or id or fileId, you will get after send message with media
                "Reuse had already sent image" prepareFor it.command.message.sender.userId sendWith ReusableMediaParams(
                    UploadType.PHOTO,
                    token = "TOKEN"
                )

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

            // when user click on button with payload "HELLO", code below will start
            answerOnCallback("ADVERT") {
                val inlineKeyboard = createAdvertKeyboard()
                "Размещение рекламы" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard

//                "Я пока ничего не умею, но скоро обязательно научусь!" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }

            // when user click on button with payload "GOODBYE", code below will start
            answerOnCallback("PLATFORM") {

//                // send message with upload Photo which replace old message
//                "Предоставление площадки" prepareReplacementCurrentMessage
//                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith
//                        UploadParams("res/busy_dog.jpg", UploadType.PHOTO)

                // send message which replace old message
                "Предоставление площадки" answerFor it.callback.callbackId

                // send notification (as Toast) for User
                "Я пока ничего не умею, но скоро обязательно научусь!" answerNotification AnswerParams(
                    it.callback.callbackId,
                    it.callback.user.userId
                )
            }

            answerOnCallback("CONSTRUCT") {
                val inlineKeyboard = createConstructorKeyboard()
                """Добро пожаловать в конструктор рекламы!
                |Для навигации используйте кнопки:
                 """.trimMargin() prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback("BACK_TO_START") {
                val inlineKeyboard = createStartKeyboard()
                "Выберите один из предложенных вариантов:" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback("ADV_LIST") {
                val inlineKeyboard = keyboard {
                    for (i in 0..2) {
                        val n = i + 1
                        +buttonRow {
                            +Button(
                                ButtonType.CALLBACK,
                                "Реклама №$n",
                                payload = "CONSTRUCT"
                            )
                        }
                    }
                    +buttonRow {
                        +Button(
                            ButtonType.CALLBACK,
                            "<- Назад",
                            payload = "BACK_TO_ADVERT"
                        )
                    }
                }
                "Ваши объявления:" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback("BACK_TO_ADVERT") {
                val inlineKeyboard = createAdvertKeyboard()
                "Размещение рекламы" prepareReplacementCurrentMessage
                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith inlineKeyboard
            }

            answerOnCallback("ADV_NAME") {

                "Plug in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
            answerOnCallback("ADV_TEXT") {

                "Plug in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
            answerOnCallback("ADV_IMG") {

                "Plug in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }
            answerOnCallback("ADV_TARGETS") {

                "Plug in progress" answerNotification AnswerParams(it.callback.callbackId, it.callback.user.userId)
            }

        }

        messages {

            // if current update is message, but not contains command, code below will start
            answerOnMessage { messageState ->
                typingOn(messageState.message.recipient.chatId)
                val result =
                    RequestSendMessage("Для начала работы введите команду /start") sendFor messageState.message.recipient.chatId
                when (result) {
                    is ResultRequest.Success -> result.response
                    is ResultRequest.Failure -> result.exception
                }
                typingOff(messageState.message.recipient.chatId)
            }

        }

        users {

            // if some user added in chat where your bot is member, code below will start
            onAddedUserToChat {
                """Привет, ${it.user.name}!
                    |Не хочешь купить немного рекламы?
                """.trimMargin() sendFor it.chatId
            }

            // if some user removed in chat where your bot is member, code below will start
            onRemovedUserFromChat {

            }

        }

    }
}

private suspend fun CommandsScope.sendTextWithKeyboard(state: CommandState, keyboard: InlineKeyboard) {
    "Choose you dinner" prepareFor state.command.message.sender.userId sendWith keyboard
}

private fun createStartKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Разместить рекламу",
                payload = "ADVERT"
            )
            +Button(
                ButtonType.CALLBACK,
                "Предоставить площадку",
                payload = "PLATFORM"
            )
        }

//
//
//        add(buttonRow {
//            add(
//                Button(
//                    ButtonType.LINK,
//                    "Find new dreams",
//                    url = "http://dreams.com/"
//                )
//            )
//        })
    }
}

private fun createAdvertKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Мои объявления",
                payload = "ADV_LIST"
            )
            +Button(
                ButtonType.CALLBACK,
                "Создать рекламу",
                payload = "CONSTRUCT"
            )
        }

        this add buttonRow {
            this add Button(
                ButtonType.CALLBACK,
                "<- Назад",
                payload = "BACK_TO_START"
            )
        }
    }
}

private fun createConstructorKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка названия",
                payload = "ADV_NAME"
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка текста",
                payload = "ADV_TEXT"
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка изображения",
                payload = "ADV_IMG"
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка каналов",
                payload = "ADV_TARGETS"
            )
        }

        this add buttonRow {
            this add Button(
                ButtonType.CALLBACK,
                "<- Назад",
                payload = "BACK_TO_ADVERT"
            )
        }
    }
}



