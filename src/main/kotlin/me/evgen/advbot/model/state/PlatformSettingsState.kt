package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.service.PlatformService

class PlatformSettingsState(timestamp: Long, private val chatId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val adPlatform = PlatformService.getPlatform(chatId)
        val chat = adPlatform?.getChatFromServer(requestsManager)
        if (chat == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        """Настройка платформы:
            |${chat.title}
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