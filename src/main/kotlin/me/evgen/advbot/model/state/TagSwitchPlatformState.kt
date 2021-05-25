package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.BotController
import me.evgen.advbot.db.DBSessionFactoryUtil
import me.evgen.advbot.getUserId

class TagSwitchPlatformState(timestamp: Long, private val chatId: Long, private val tag: String) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val adPlatform = DBSessionFactoryUtil.localStorage.getPlatform(callbackState.getUserId(), chatId)
        if (adPlatform == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        DBSessionFactoryUtil.localStorage.tagSwitchPlatform(callbackState.getUserId(), adPlatform, tag)

        "Тег успешно изменен".answerNotification(
            callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)

        val newState = TagSelectionPlatformState(timestamp, chatId)
        BotController.moveTo(newState, callbackState.getUserId().id) {
            newState.handle(callbackState, prevState, requestsManager)
        }
    }
}