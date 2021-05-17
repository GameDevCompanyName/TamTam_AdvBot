package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.createBackKeyboard
import me.evgen.advbot.model.navigation.Payload

class PlatformState(timestamp: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        "Выбирите платформу, которую хотите предоставить для размещения рекламы:".answerWithKeyboard(
            callbackState.callback.callbackId,
            createBackKeyboard(
                Payload(StartState::class, StartState(timestamp).toJson())
            ),
            requestsManager
        )
    }
}
