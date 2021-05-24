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
import me.evgen.advbot.model.navigation.Payload

class PlatformSettingsState(timestamp: Long, private val chatId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val adPlatform = DBSessionFactoryUtil.localStorage.getPlatform(callbackState.getUserId(), chatId)
        if (adPlatform == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        """Настройка платформы:
            |${adPlatform.getChatTitle()}
            |
            |Текущие теги:
            |${adPlatform.getTagsString()}
            |Текущие параметры доступа к рекламе:
            |${adPlatform.getAvailability()}
            |""".trimMargin().answerWithKeyboard(
            callbackState.callback.callbackId,
            createKeyboard(),
            requestsManager
        )
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка тегов 🏷",
                    payload = Payload(
                        TagSelectionPlatformState::class,
                        TagSelectionPlatformState(timestamp, chatId).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка доступа 🔒",
                    payload = Payload(
                        PlatformAccessSettingsState::class,
                        PlatformAccessSettingsState(timestamp, chatId).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +getBackButton(
                    Payload(
                        PlatformListState::class, PlatformListState(
                            timestamp
                        ).toJson()
                    )
                )
            }
        }
    }
}