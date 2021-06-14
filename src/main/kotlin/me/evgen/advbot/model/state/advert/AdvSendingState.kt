package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.AttachType
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.request.AttachmentPhotoWithUrl
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.model.request.PayloadUrl
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.botText
import me.evgen.advbot.emoji.Emoji
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.ErrorType
import me.evgen.advbot.model.entity.Campaign
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.AdvertService

class AdvSendingState(
    timestamp: Long,
    private val advertId: Long,
    private val chatId: Long
) : BaseState(timestamp), CustomCallbackState {

    override suspend fun handle(callbackState: CallbackState, requestsManager: RequestsManager) {
        val advert = AdvertService.findAdvert(advertId)
        if (advert != null) {
            val attachment: AttachmentPhotoWithUrl? = if (advert.mediaUrl.isNotEmpty()) {
                AttachmentPhotoWithUrl(AttachType.IMAGE.value, PayloadUrl(advert.mediaUrl))
            } else {
                null
            }
            val postId = "${advert.text}${botText()}".sendThroughTech(
                ChatId(chatId),
                attachment,
                requestsManager
            )
            if (postId != null) {
                val post = Campaign(postId, advert)
                AdvertService.addPost(post)

                "${Emoji.FIRECRACKER} Рекламное объявление \"${advert.title}\" успешно отправлено.".sendTo(
                    callbackState.getUserId(),
                    requestsManager
                )

                val newState = AdvListState(timestamp)
                BotController.moveTo(newState, callbackState.getUserId().id) {
                    newState.handle(callbackState, requestsManager)
                }
            } else {
                ErrorType.POST_ADVERT.errorMessage.sendToUserWithKeyboard(
                    callbackState.getUserId(),
                    createErrorKeyboard(),
                    requestsManager
                )
            }
        } else {
            ErrorType.POST_ADVERT.errorMessage.sendToUserWithKeyboard(
                callbackState.getUserId(),
                createErrorKeyboard(),
                requestsManager
            )
        }
    }

    private fun createErrorKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +CallbackButton.DEFAULT_CANCEL.create(
                    AdvState(timestamp, advertId).toPayload()
                )
                +CallbackButton.REPEAT.create(
                    AdvSendingState(timestamp, advertId, chatId).toPayload()
                )
            }
        }
    }
}
