package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.Payloads
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.storage.LocalStorage

class TagSelectionState(timestamp: Long, private val chatId: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager) {
        val adPlatform = LocalStorage.getPlatform(callbackState.getUserId(), chatId)
        if (adPlatform == null) {
            "Ошибка! Нет такой платформы.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        """Настройка тегов для платформы:
            | ${adPlatform.getChatTitle()}
            | 
            | Для добавления тега нажмите на соответствующую кнопку.
            | 
            | Список текущих тегов:
            | ${adPlatform.getTagsString()}""".trimMargin().answerWithKeyboard(
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
                    "Спорт",
                    payload = Payloads.WIP
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Игры",
                    payload = Payloads.WIP
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