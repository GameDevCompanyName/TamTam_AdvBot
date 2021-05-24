package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.MessageState
import me.evgen.advbot.Payloads
import me.evgen.advbot.getCancelButton
import me.evgen.advbot.getDoneButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.entity.TempAdvert
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.service.AdvertService

class AdvConstructorState(
    timestamp: Long,
    private val advertId: Long,
    private val isCreatingAdvert: Boolean
) : BaseState(timestamp), CustomCallbackState, CustomMessageState {

    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        var message = "ОШИБКА"

        if (!isCreatingAdvert) {
            val advert = AdvertService.findAdvert(advertId)
            if (advert != null) {
                val tempAdvert = TempAdvert(advert)
                AdvertService.addTempAdvert(tempAdvert)
                message = createMessage(tempAdvert.title, tempAdvert.text)
            }
        }

        message.answerWithKeyboard(callbackState.callback.callbackId, createKeyboard(), requestsManager)
    }

    override suspend fun handle(messageState: MessageState, requestsManager: RequestsManager) {
        val tempAdvert = AdvertService.findTempAdvertByUserId(messageState.getUserId().id)
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
                        AdvTitlingState(timestamp, advertId, isCreatingAdvert).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка текста 📃",
                    payload = Payload(
                        AdvTextingState::class,
                        AdvTextingState(timestamp, advertId, isCreatingAdvert).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка изображения 📺",
                    payload = Payloads.WIP
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка каналов 📢",
                    payload = Payloads.WIP
                )
            }

            +buttonRow {
                val prevCancelPayload = if (isCreatingAdvert) {
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
                +getCancelButton(prevCancelPayload, needNegativeIntent = true)
                +getDoneButton(
                    Payload(
                        SaveAdvertState::class, SaveAdvertState(
                            timestamp,
                            advertId,
                            isCreatingAdvert
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
