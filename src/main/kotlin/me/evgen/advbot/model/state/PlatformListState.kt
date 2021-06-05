package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.prepared.Chat
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserIdLong
import me.evgen.advbot.service.PlatformService

class PlatformListState(timestamp: Long) : BaseState(timestamp), CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val adPlatform = PlatformService.getPlatforms(callbackState.getUserIdLong())

        val chats = mutableSetOf<Chat>()

        for (entry in adPlatform) {
            entry.getChatFromServer(requestsManager)?.apply {
                chats.add(this)
            }
        }

        val inlineKeyboard = createKeyboard(chats)

        """Выберите платформу для настройки размещения.
            |Для того, чтобы платформа появилась в списке, добавьте бота @AdvertizerBot в свой чат или канал.
        """.trimMargin().answerWithKeyboard(
            callbackState.callback.callbackId,
            inlineKeyboard,
            requestsManager
        )
    }

    private fun createKeyboard(chats: Collection<Chat>): InlineKeyboard {
        return keyboard {
            for (entry in chats) {
                +buttonRow {
                    +Button(
                        ButtonType.CALLBACK,
                        entry.title,
                        payload = Payload(
                            PlatformSettingsState::class,
                            PlatformSettingsState(timestamp, entry.chatId.id).toJson()
                        ).toJson()
                    )
                }
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
