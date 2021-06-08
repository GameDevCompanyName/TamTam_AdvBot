package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.AdvertService

class TagSwitchAdvertState(timestamp: Long, private val advertId: Long, private val tagId: Long) : BaseState(timestamp),
    CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val advert = AdvertService.findAdvert(advertId)
        if (advert == null) {
            "Ошибка! Нет такого объявления.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        AdvertService.tagSwitchAdvert(advert, tagId)

        "Тег успешно изменен".answerNotification(
            callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)

        val newState =
            TagSelectionAdvertState(timestamp, advertId)
        BotController.moveTo(newState, callbackState.getUserId().id) {
            newState.handle(callbackState, requestsManager)
        }
    }
}