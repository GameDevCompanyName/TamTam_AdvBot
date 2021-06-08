package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.AdvertService

class AdvDeletedState(timestamp: Long, private val advertId: Long) : BaseState(timestamp),
    CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        AdvertService.deleteAdvert(advertId)

        val newState = AdvListState(timestamp)
        BotController.moveTo(newState, callbackState.getUserId().id) {
            newState.handle(callbackState, requestsManager)
        }
    }
}
