package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUser
import me.evgen.advbot.storage.LocalStorage

class SaveAdvertState(timestamp: Long, private val advertId: Long?) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val tempAdvert = BotController.tempAdMap[callbackState.getUser()]
        if (tempAdvert != null) {
            if (advertId == null) {
                LocalStorage.addAdvert(callbackState.getUser(), tempAdvert)

                val newState = AdvListState(timestamp)
                BotController.moveTo(newState, callbackState.getUser()) {
                    newState.handle(callbackState, prevState, requestsManager)
                }
            } else {
                LocalStorage.updateAdvert(callbackState.getUser(), advertId, tempAdvert)

                val newState = AdvState(timestamp, advertId)
                BotController.moveTo(newState, callbackState.getUser()) {
                    newState.handle(callbackState, prevState, requestsManager)
                }
            }

            BotController.tempAdMap.remove(callbackState.getUser())
        } else {
            //TODO action
            //TODO log
        }
    }
}