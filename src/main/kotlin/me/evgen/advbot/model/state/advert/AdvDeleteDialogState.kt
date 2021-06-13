package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonIntent
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.getBackButton
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.AdvertService

class AdvDeleteDialogState(timestamp: Long, private val advertId: Long) : BaseState(timestamp),
    CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val advert = AdvertService.findAdvert(advertId)
        if (advert == null) {
            "Не удалось обнаружить такую рекламу.".answerWithKeyboard(callbackState.callback.callbackId, createErrorKeyboard(), requestsManager)
            return
        }

        """Вы уверены, что хотитет удалить рекламу?
            | Название: ${advert.title}
            | Текст:
            | ${advert.text}
        """.trimMargin().answerWithKeyboard(callbackState.callback.callbackId, createKeyboard(), requestsManager)
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +CallbackButton.DEFAULT_CANCEL.create(
                    AdvState(timestamp, advertId).toPayload()
                )
                +Button(
                    ButtonType.CALLBACK,
                    "Удалить",
                    payload = Payload(
                        AdvDeletedState::class,
                        AdvDeletedState(timestamp, advertId).toJson()
                    ).toJson(),
                    intent = ButtonIntent.NEGATIVE
                )
            }
        }
    }

    private fun createErrorKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +getBackButton(
                    Payload(
                        AdvState::class, AdvState(
                            timestamp,
                            advertId
                        ).toJson())
                )
            }
        }
    }
}
