package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.*
import chat.tamtam.botsdk.model.request.AttachmentPhotoWithUrl
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.model.request.PayloadUrl
import chat.tamtam.botsdk.state.CallbackState
import kotlinx.coroutines.runBlocking
import me.evgen.advbot.BotController
import me.evgen.advbot.botText
import me.evgen.advbot.emoji.Emoji
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.entity.Campaign
import me.evgen.advbot.model.payment.PaymentMapping
import me.evgen.advbot.model.payment.PaymentStatusEvent
import me.evgen.advbot.model.state.advert.AdvChoosePlatform
import me.evgen.advbot.model.state.advert.AdvListState
import me.evgen.advbot.model.state.advert.AdvSendingState
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

    //TODO это временная мера
    var lastCallbackState: CallbackState? = null

    override fun propertyChange(evt: PropertyChangeEvent) {
        if (evt.propertyName == PaymentStatusEvent.PROPERTY_NAME && evt.newValue is PaymentStatusEvent) {
            runBlocking {
                val advert = AdvertService.findAdvert(advertId)

                val paymentStatusEvent = evt.newValue as PaymentStatusEvent

                if (advert != null) {
                    val attachment: AttachmentPhotoWithUrl? = if (advert.mediaUrl.isNotEmpty()) {
                        AttachmentPhotoWithUrl(AttachType.IMAGE.value, PayloadUrl(advert.mediaUrl))
                    } else {
                        null
                    }
                    val postId = "${advert.text}${botText()}".sendThroughTech(
                        ChatId(chatId),
                        attachment,
                        paymentStatusEvent.requestsManager
                    )

                    if (postId != null) {
                        val post = Campaign(postId, advert)
                        AdvertService.addPost(post)
                    }

                    "${Emoji.FIRECRACKER} Рекламное объявление \"${advert.title}\" успешно отправлено.".sendTo(
                        UserId(advert.owner.id),
                        paymentStatusEvent.requestsManager
                    )

                    if (lastCallbackState != null) {
                        val newState = AdvListState(timestamp)
                        BotController.moveTo(newState, advert.owner.id) {
                            newState.handle(lastCallbackState!!, paymentStatusEvent.requestsManager)
                        }
                    }
                }
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

        lastCallbackState = callbackState
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +CallbackButton.BACK.create( //TODO надо возвращать на ту же страницу где он был, а не в начало
                    AdvChoosePlatform(timestamp, PlatformsForPostingArgs(advertId)).toPayload()
                )
                +Button(
                    ButtonType.CALLBACK,
                    "Оплатить",
                    intent = ButtonIntent.POSITIVE,
                    payload = AdvSendingState(timestamp, advertId, chatId).toPayload().toJson()
                )
            }
        }
    }
}