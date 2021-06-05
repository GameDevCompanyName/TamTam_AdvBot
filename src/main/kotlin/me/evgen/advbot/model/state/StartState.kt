package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.CommandState
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload

class StartState(timestamp: Long) : BaseState(timestamp), CustomCallbackState, CustomCommandState {

    override suspend fun handle(callbackState: CallbackState, requestsManager: RequestsManager) {
        val inlineKeyboard = createKeyboard()

        "Выберите один из предложенных вариантов:".answerWithKeyboard(callbackState.callback.callbackId, inlineKeyboard, requestsManager)
    }

    override suspend fun handle(commandState: CommandState, requestsManager: RequestsManager) {
        val userId = commandState.getUserId()
        val inlineKeyboard = createKeyboard()

        "Вы можете разместить рекламу или предоставить площадку для ее размещения".sendTo(userId, requestsManager)
        "Выберите один из предложенных вариантов:".sendToUserWithKeyboard(userId, inlineKeyboard, requestsManager)
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Разместить рекламу",
                    payload = Payload(
                        MenuAdvertState::class,
                        MenuAdvertState(this@StartState.timestamp).toJson()
                    ).toJson()
                )
                +Button(
                    ButtonType.CALLBACK,
                    "Предоставить площадку",
                    payload = Payload(
                        PlatformListState::class,
                        PlatformListState(this@StartState.timestamp)
                            .toJson()
                    ).toJson()
                )
            }
        }
    }
}
