package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUser
import me.evgen.advbot.getUserId
import me.evgen.advbot.storage.LocalStorage

class PlatformAccessSwitchState(timestamp: Long, private val chatId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val adPlatform = LocalStorage.getPlatform(callbackState.getUserId(), chatId)
        if (adPlatform == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        adPlatform.accessSwitch()

        "Режим доступа к платформе успешно изменен".answerNotification(
            callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)

        val newState = PlatformAccessSettingsState(timestamp, chatId)
        BotController.moveTo(newState, callbackState.getUser()) {
            newState.handle(callbackState, prevState, requestsManager)
        }
    }
}