package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.response.Permissions
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.botText
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.AdvertService
import me.evgen.advbot.service.PlatformService

class AdvSendingState(timestamp: Long, private val advertId: Long) : BaseState(timestamp),
    CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val platforms = PlatformService.getAllPlatforms()
        val advert = AdvertService.findAdvert(advertId)
        if (advert != null) {
            val message = "${advert.text}${botText()}"
            for (entry in platforms) {
                if (entry.availability) {
                    when (val res = requestsManager.getMembershipInfoInChat(ChatId(entry.id))) {
                        is ResultRequest.Success -> {
                            if (res.response.permissions!!.contains(Permissions.WRITE)) {
                                message.sendTo(ChatId(entry.id), requestsManager)
                            }
                        }
                    }
                }
            }
        }

        "Рекламное объявление успешно отправлено".answerNotification(
            callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)

        val newState = AdvState(timestamp, advertId)
        BotController.moveTo(newState, callbackState.getUserId().id) {
            newState.handle(callbackState, requestsManager)
        }
    }
}