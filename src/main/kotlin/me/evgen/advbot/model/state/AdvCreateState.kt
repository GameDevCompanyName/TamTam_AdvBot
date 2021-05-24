package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.MessageState
import me.evgen.advbot.BotController
import me.evgen.advbot.db.DBSessionFactoryUtil
import me.evgen.advbot.getCancelButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.entity.TempAdvert
import me.evgen.advbot.service.AdvertService
import me.evgen.advbot.service.UserService

class AdvCreateState(timestamp: Long) : BaseState(timestamp), CustomCallbackState, MessageListener {

    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val inlineKeyboard = createKeyboard(prevState)

        """Введите название будущей рекламы.
            |Название используется для идентификации и в самом объявлении показано не будет.
        """.trimMargin().answerWithKeyboard(callbackState.callback.callbackId, inlineKeyboard, requestsManager)
    }

    override suspend fun onMessageReceived(messageState: MessageState, requestsManager: RequestsManager) {
        val user = UserService.findUser(messageState.getUserId().id) ?: return
        val tempAdvert = TempAdvert(
            DBSessionFactoryUtil.localStorage.getNextId(),
            messageState.message.body.text,
            "",
            user
        )
        AdvertService.addTempAdvert(tempAdvert)

        val newState = AdvConstructorState(timestamp, tempAdvert.id, isCreatingAdvert = true)
        BotController.moveTo(newState, messageState.getUserId().id) {
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
