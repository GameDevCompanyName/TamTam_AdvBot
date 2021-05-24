package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUserId
import me.evgen.advbot.service.AdvertService
import me.evgen.advbot.model.entity.Advert

class SaveAdvertState(
    timestamp: Long,
    private val advertId: Long,
    private val isCreatingAdvert: Boolean
) : BaseState(timestamp), CustomCallbackState {

    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val tempAdvert = AdvertService.findTempAdvertByUserId(callbackState.getUserId().id)
        if (tempAdvert != null) {
            val advert = Advert(tempAdvert)
            if (isCreatingAdvert) {
                AdvertService.addAdvert(advert)

                val newState = AdvListState(timestamp)
                BotController.moveTo(newState, callbackState.getUserId().id) {
                    newState.handle(callbackState, prevState, requestsManager)
                }
            } else {
                AdvertService.updateAdvert(advert)

                val newState = AdvState(timestamp, advertId)
                BotController.moveTo(newState, callbackState.getUserId().id) {
                    newState.handle(callbackState, prevState, requestsManager)
                }
            }

            AdvertService.deleteTempAdvertByUserId(tempAdvert.owner.id)
        } else {
            //TODO action
            //TODO log
        }
    }
}