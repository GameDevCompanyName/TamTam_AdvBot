package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonIntent
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.Payloads
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.service.AdvertService

class AdvState(timestamp: Long, private val advertId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val advert = AdvertService.findAdvert(advertId)
        if (advert == null) {
            "Ошибка! Нет такой рекламы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        """Работа с рекламой:
            | ${advert.title}""".trimMargin().answerWithKeyboard(
            callbackState.callback.callbackId,
            createKeyboard(),
            requestsManager
        )
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настроить рекламу ⚙",
                    payload = Payload(
                        AdvConstructorState::class,
                        AdvConstructorState(timestamp, advertId, isCreatingAdvert = false).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Просмотр метрик 📊",
                    payload = Payloads.WIP
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Отключить рекламу 🔒",
                    payload = Payloads.WIP
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Удалить рекламу 🗑",
                    intent = ButtonIntent.NEGATIVE,
                    payload = Payload(
                        AdvDeleteDialogState::class,
                        AdvDeleteDialogState(timestamp, advertId).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +getBackButton(
                    Payload(
                        AdvListState::class, AdvListState(
                            timestamp
                        ).toJson()
                    )
                )
            }
        }
    }
}