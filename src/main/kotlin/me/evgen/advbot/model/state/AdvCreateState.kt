package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.MessageState
import me.evgen.advbot.BotController
import me.evgen.advbot.getCancelButton
import me.evgen.advbot.model.TempAdvert

class AdvCreateState(timestamp: Long) : BaseState(timestamp), CustomCallbackState, MessageListener {

    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val inlineKeyboard = createKeyboard(prevState)

        """Введите название будущей рекламы.
            |Название используется для идентификации и в самом объявлении показано не будет.
        """.trimMargin().answerWithKeyboard(callbackState.callback.callbackId, inlineKeyboard, requestsManager)
    }

    override suspend fun onMessageReceived(messageState: MessageState, requestsManager: RequestsManager) {
        val tempAdvert = TempAdvert().apply {
            title = messageState.message.body.text
        }
        BotController.tempAdMap[messageState.message.sender] = tempAdvert

        val newState = AdvConstructorState(timestamp, null)
        BotController.moveTo(newState, messageState.message.sender) {
            newState.handle(messageState, requestsManager)
        }
    }

    private fun createKeyboard(previousState: BaseState?): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +getCancelButton(getOrDefaultPayload(previousState))
            }
        }
    }
}
