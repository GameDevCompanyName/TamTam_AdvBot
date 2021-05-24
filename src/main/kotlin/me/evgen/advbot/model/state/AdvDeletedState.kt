package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUserId
import me.evgen.advbot.service.AdvertService

class AdvDeletedState(timestamp: Long, private val advertId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        AdvertService.deleteAdvert(advertId)

        val newState = AdvListState(timestamp)
        BotController.moveTo(newState, callbackState.getUserId().id) {
            newState.handle(callbackState, prevState, requestsManager)
        }
    }
}
