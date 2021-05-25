package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUserId
import me.evgen.advbot.getUserIdLong
import me.evgen.advbot.service.PlatformService

class TagSwitchPlatformState(timestamp: Long, private val chatId: Long, private val tag: String) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val adPlatform = PlatformService.getPlatform(callbackState.getUserIdLong(), chatId)
        if (adPlatform == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        PlatformService.tagSwitchPlatform(callbackState.getUserIdLong(), adPlatform, tag)

        "Тег успешно изменен".answerNotification(
            callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)

        val newState = TagSelectionPlatformState(timestamp, chatId)
        BotController.moveTo(newState, callbackState.getUserId().id) {
            newState.handle(callbackState, prevState, requestsManager)
        }
    }
}