package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.model.AttachType
import chat.tamtam.botsdk.model.prepared.AttachmentMedia
import chat.tamtam.botsdk.model.prepared.AttachmentPhoto
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.MessageState
import me.evgen.advbot.BotController
import me.evgen.advbot.createCancelKeyboard
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.model.state.MessageListener
import me.evgen.advbot.service.AdvertService

class AdvMediaState(
    timestamp: Long,
    private val advertId: Long,
    private val isCreatingAdvert: Boolean
) : BaseState(timestamp),
    CustomCallbackState,
    MessageListener {

    override suspend fun handle(callbackState: CallbackState, requestsManager: RequestsManager) {
        val inlineKeyboard = createCancelKeyboard(
            Payload(
                AdvConstructorState::class, AdvConstructorState(
                    timestamp,
                    advertId,
                    isCreatingAdvert
                ).toJson()
            )
        )
        "Прикрепите изображение.".sendToUserWithKeyboard(callbackState.getUserId(), inlineKeyboard, requestsManager)
    }

    override suspend fun onMessageReceived(messageState: MessageState, requestsManager: RequestsManager) {
        if (messageState.message.body.attachments.isNotEmpty()) {
            var attachment = messageState.message.body.attachments[0]
            if (attachment.type == AttachType.IMAGE) {
                attachment = attachment as AttachmentPhoto
                val advert = AdvertService.findAdvert(advertId)
                if (advert != null) {
                    advert.mediaUrl = attachment.payload.url
                    AdvertService.updateAdvert(advert)
                }
                val newState = AdvConstructorState(
                    timestamp,
                    advertId,
                    isCreatingAdvert
                )
                BotController.moveTo(newState, messageState.getUserId().id) {
                    newState.handle(messageState, requestsManager)
                }
            } else {
                "Тип приложенного файла не соответствует требованиям.".sendTo(messageState.getUserId(), requestsManager)
            }

        } else {
            "В сообщении не найдено вложений.".sendTo(messageState.getUserId(), requestsManager)
        }

    }
}