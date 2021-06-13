package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.UserId
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import kotlinx.coroutines.runBlocking
import me.evgen.advbot.botText
import me.evgen.advbot.emoji.Emoji
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.payment.PaymentMapping
import me.evgen.advbot.model.payment.PaymentStatusEvent
import me.evgen.advbot.model.state.advert.AdvChoosePlatform
import me.evgen.advbot.service.AdvertService
import me.evgen.advbot.service.PlatformService
import me.evgen.advbot.service.PlatformsForPostingArgs
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class PayPostingState(
    timestamp: Long,
    private val advertId: Long,
    private val chatId: Long,
    private val cost: Int
) : BaseState(timestamp), CustomCallbackState, PropertyChangeListener {

    override fun propertyChange(evt: PropertyChangeEvent) {
        if (evt.propertyName == PaymentStatusEvent.PROPERTY_NAME && evt.newValue is PaymentStatusEvent) {
            runBlocking {
                val advert = AdvertService.findAdvert(advertId)

                val paymentStatusEvent = evt.newValue as PaymentStatusEvent

                "${advert!!.text}${botText()}".sendTo(ChatId(chatId), paymentStatusEvent.requestsManager)

                //TODO нужно получать правильный айди
//                "${Emoji.FIRECRACKER} Рекламное объявление \"${advert.title}\" успешно отправлено.".sendTo(
//                    UserId(advert.owner.id),
//                    paymentStatusEvent.requestsManager
//                )
            }
        }
    }

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
                    AdvChoosePlatform(timestamp, PlatformsForPostingArgs(advertId)).toPayload()
                )
                +Button(
                    ButtonType.LINK,
                    "Оплатить",
                    url = "http://localhost:8080/${PaymentMapping.PAY_SUCCESS}"
                )
            }
        }
    }
}