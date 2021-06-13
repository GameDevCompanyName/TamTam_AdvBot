package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.getBackButton
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.model.state.StartState

class MenuAdvertState(timestamp: Long) : BaseState(timestamp),
    CustomCallbackState {

    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val inlineKeyboard = createKeyboard()

        "Размещение рекламы".answerWithKeyboard(callbackState.callback.callbackId, inlineKeyboard, requestsManager)
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Мои объявления",
                    payload = Payload(
                        AdvListState::class,
                        AdvListState(timestamp).toJson()
                    ).toJson()
                )
                +Button(
                    ButtonType.CALLBACK,
                    "Создать рекламу",
                    payload = Payload(
                        AdvCreateState::class,
                        AdvCreateState(timestamp).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +getBackButton(
                    Payload(
                        StartState::class, StartState(
                            timestamp
                        ).toJson()
                    )
                )
            }
        }
    }
}
