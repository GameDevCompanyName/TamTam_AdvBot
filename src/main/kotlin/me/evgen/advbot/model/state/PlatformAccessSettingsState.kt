package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.db.DBSessionFactoryUtil
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.AdPlatform
import me.evgen.advbot.model.navigation.Payload

class PlatformAccessSettingsState(timestamp: Long, private val chatId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val adPlatform = DBSessionFactoryUtil.localStorage.getPlatform(callbackState.getUserId(), chatId)
        if (adPlatform == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        """Настройка доступности платформы:
            | ${adPlatform.getChatTitle()}
            | Текущие параметры доступа к рекламе:
            | ${adPlatform.getAvailability()}""".trimMargin().answerWithKeyboard(
            callbackState.callback.callbackId,
            createKeyboard(adPlatform),
            requestsManager
        )
    }

    private fun createKeyboard(adPlatform: AdPlatform): InlineKeyboard {
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