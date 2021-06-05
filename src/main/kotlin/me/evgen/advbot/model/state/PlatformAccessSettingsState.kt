package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.entity.IPlatform
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.service.PlatformService

class PlatformAccessSettingsState(timestamp: Long, private val chatId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val adPlatform = PlatformService.getPlatform(chatId)
        val chat = adPlatform?.getChatFromServer(requestsManager)
        if (chat == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        """Настройка доступности платформы:
            | ${chat.title}
            | Текущие параметры доступа к рекламе:
            | ${adPlatform.getAvailability()}""".trimMargin().answerWithKeyboard(
            callbackState.callback.callbackId,
            createKeyboard(adPlatform),
            requestsManager
        )
    }

    private fun createKeyboard(adPlatform: IPlatform): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    adPlatform.getAccessButton(),
                    payload = Payload(
                        PlatformAccessSwitchState::class,
                        PlatformAccessSwitchState(timestamp, chatId).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +getBackButton(
                    Payload(
                        PlatformSettingsState::class, PlatformSettingsState(
                            timestamp,
                            chatId
                        ).toJson()
                    )
                )
            }
        }
    }
}