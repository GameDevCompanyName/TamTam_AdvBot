package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUser
import me.evgen.advbot.storage.LocalStorage

class AdvDeletedState(timestamp: Long, private val advertId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        LocalStorage.deleteAdvert(callbackState.getUser(), advertId)

        val newState = AdvListState(timestamp)
        BotController.moveTo(newState, callbackState.getUser()) {
            newState.handle(callbackState, prevState, requestsManager)
        }
    }
}
