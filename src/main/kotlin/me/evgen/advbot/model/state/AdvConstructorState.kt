package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.MessageState
import me.evgen.advbot.BotController
import me.evgen.advbot.Payloads
import me.evgen.advbot.getCancelButton
import me.evgen.advbot.getDoneButton
import me.evgen.advbot.getUser
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.TempAdvert
import me.evgen.advbot.storage.LocalStorage

class AdvConstructorState(timestamp: Long, private val advertId: Long?) : BaseState(timestamp), CustomCallbackState, CustomMessageState {

    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        var message = "ОШИБКА"

        if (advertId != null) {
            val advert = LocalStorage.getAd(callbackState.callback.user, advertId)
            if (advert != null) {
                val tempAdvert = TempAdvert(advert)
                BotController.tempAdMap[callbackState.getUser()] = tempAdvert
                message = createMessage(tempAdvert.title, tempAdvert.text)
            }
        }

        message.answerWithKeyboard(callbackState.callback.callbackId, createKeyboard(), requestsManager)
    }

    override suspend fun handle(
        messageState: MessageState,
        requestsManager: RequestsManager
    ) {
        val tempAdvert = BotController.tempAdMap[messageState.message.sender]
        val message = if (tempAdvert != null) {
            createMessage(tempAdvert.title, tempAdvert.text)
        } else {
            "ОШИБКА"
            }

        message.sendToUserWithKeyboard(messageState.getUserId(), createKeyboard(), requestsManager)
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка названия ✏",
                    payload = Payload(
                        AdvTitlingState::class,
                        AdvTitlingState(timestamp, advertId).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка текста 📃",
                    payload = Payload(
                        AdvTextingState::class,
                        AdvTextingState(timestamp, advertId).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка изображения 📺",
                    payload = Payloads.ADV_IMG
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка каналов 📢",
                    payload = Payloads.ADV_TARGETS
                )
            }

            +buttonRow {
                val prevCancelPayload = if (advertId == null) {
                    Payload(
                        MenuAdvertState::class, MenuAdvertState(
                            timestamp
                        ).toJson()
                    )
                } else {
                    Payload(
                        AdvState::class, AdvState(
                            timestamp,
                            advertId
                        ).toJson()
                    )
                }
                +getCancelButton(prevCancelPayload)
                +getDoneButton(
                    Payload(
                        SaveAdvertState::class, SaveAdvertState(
                            timestamp,
                            advertId
                        ).toJson()
                    )
                )
            }
        }
    }

    private fun createMessage(title: String, text: String): String {
        val tempTitle = """Текущее название рекламы:
        |$title""".trimMargin()
        val tempText = if (text.isBlank()) {
            ""
        } else {
            """Текущий текст рекламы:
        |$text""".trimMargin()
        }

        return """$tempTitle
        |$tempText""".trimMargin()
    }
}
