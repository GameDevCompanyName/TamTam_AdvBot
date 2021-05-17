package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.MessageState
import me.evgen.advbot.BotController
import me.evgen.advbot.createCancelKeyboard
import me.evgen.advbot.getUser
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload

class AdvTextingState(timestamp: Long, private val advertId: Long?) : BaseState(timestamp), CustomCallbackState, MessageListener {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val inlineKeyboard = createCancelKeyboard(
            Payload(
                AdvConstructorState::class, AdvConstructorState(
                    timestamp,
                    advertId
                ).toJson()
            )
        )
        "Введите новый текст рекламы.".sendToUserWithKeyboard(callbackState.getUserId(), inlineKeyboard, requestsManager)
    }

    override suspend fun onMessageReceived(messageState: MessageState, requestsManager: RequestsManager) {
        val newText = messageState.message.body.text
        val tempAdvert = BotController.tempAdMap[messageState.getUser()]
        if (tempAdvert != null) {
            tempAdvert.text = newText
        }

        val newState = AdvConstructorState(timestamp, advertId)
        BotController.moveTo(newState, messageState.getUser()) {
            newState.handle(messageState, requestsManager)
        }
    }
}