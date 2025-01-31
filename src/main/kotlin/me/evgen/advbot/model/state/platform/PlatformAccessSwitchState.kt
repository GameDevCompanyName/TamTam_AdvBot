package me.evgen.advbot.model.state.platform

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.PlatformService

class PlatformAccessSwitchState(timestamp: Long, private val chatId: Long) : BaseState(timestamp),
    CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val adPlatform = PlatformService.getPlatform(chatId)
        if (adPlatform == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        PlatformService.accessSwitch(adPlatform)

        "Режим доступа к платформе успешно изменен".answerNotification(
            callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)

        val newState =
            PlatformAccessSettingsState(timestamp, chatId)
        BotController.moveTo(newState, callbackState.getUserId().id) {
            newState.handle(callbackState, requestsManager)
        }
    }
}