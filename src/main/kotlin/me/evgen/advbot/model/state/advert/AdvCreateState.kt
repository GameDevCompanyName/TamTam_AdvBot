package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.MessageState
import me.evgen.advbot.BotController
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.ErrorType
import me.evgen.advbot.model.entity.Advert
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.model.state.MessageListener
import me.evgen.advbot.service.AdvertService
import me.evgen.advbot.service.UserService

class AdvCreateState(timestamp: Long) : BaseState(timestamp), CustomCallbackState, MessageListener {

    override suspend fun handle(callbackState: CallbackState, requestsManager: RequestsManager) {
        val inlineKeyboard = createKeyboard()

        """Введите название будущей рекламы.
            |Название используется для идентификации и в самом объявлении показано не будет.
        """.trimMargin().answerWithKeyboard(callbackState.callback.callbackId, inlineKeyboard, requestsManager)
    }

    override suspend fun onMessageReceived(messageState: MessageState, requestsManager: RequestsManager) {
        val user = UserService.findUser(messageState.getUserId().id) ?: return
        val newAdvert = Advert(
            messageState.message.body.text,
            user
        )
        val id = AdvertService.addAdvert(newAdvert)
        if (id != null) {
            val newState = AdvConstructorState(
                timestamp,
                id,
                isCreatingAdvert = true
            )
            BotController.moveTo(newState, messageState.getUserId().id) {
                newState.handle(messageState, requestsManager)
            }
        } else {
            ErrorType.CREATE_ADVERT.errorMessage.sendTo(messageState.getUserId(), requestsManager)
            //TODO log error
        }
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +CallbackButton.DEFAULT_CANCEL.create(
                    MenuAdvertState(timestamp).toPayload()
                )
            }
        }
    }
}
