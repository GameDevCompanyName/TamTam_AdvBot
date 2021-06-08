package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonIntent
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.Payloads
import me.evgen.advbot.emoji.Emoji
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.state.advert.AdvChoosePlatform
import me.evgen.advbot.model.state.advert.AdvSendingState
import me.evgen.advbot.service.AdvertService
import me.evgen.advbot.service.PlatformService

class PayPostingState(
    timestamp: Long,
    private val advertId: Long,
    private val chatId: Long,
    private val cost: Int
) : BaseState(timestamp), CustomCallbackState {

    override suspend fun handle(callbackState: CallbackState, requestsManager: RequestsManager) {
        val chat = PlatformService.getPlatform(chatId)?.getChatFromServer(requestsManager)
        val advert = AdvertService.findAdvert(advertId)
        if (chat == null) {
            //TODO
            return
        }
        if (advert == null) {
            //TODO
            return
        }

        """Реклама: ${advert.title}
            |Канал: ${chat.title}
            |
            |К оплате: $cost руб.
            |
            |${Emoji.INFO} Вы будете перенаправленны на сайт для оплаты. После подтверждения операции реклама будет опубликована автоматически."""
            .trimMargin()
            .sendToUserWithKeyboard(
                callbackState.getUserId(),
                createKeyboard(),
                requestsManager
            )
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +CallbackButton.BACK.create( //TODO надо возвращать на ту же страницу где он был, а не в начало
                    AdvChoosePlatform(timestamp, advertId, true).toPayload()
                )
                +Button(
                    ButtonType.CALLBACK,
                    "Оплатить",
                    payload = AdvSendingState(
                        timestamp,
                        advertId,
                        chatId
                    ).toPayload().toJson(),
                    intent = ButtonIntent.POSITIVE
                )
            }
        }
    }
}