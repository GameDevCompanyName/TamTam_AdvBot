package me.evgen.advbot.model.state.platform

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonIntent
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.entity.IPlatform
import me.evgen.advbot.model.Tags
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.PlatformService

class TagSelectionPlatformState(timestamp: Long, private val chatId: Long) : BaseState(timestamp),
    CustomCallbackState {
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

        """Настройка тегов для платформы:
            |${chat.title}
            | 
            |Чтобы добавить или удалить тег нажмите на соответствующую кнопку.
            |""".trimMargin().answerWithKeyboard(
            callbackState.callback.callbackId,
            createKeyboard(adPlatform),
            requestsManager
        )
    }

    private fun createKeyboard(adPlatform: IPlatform): InlineKeyboard {
        return keyboard {
            for (entry in Tags.getAllTags()) {
                +buttonRow {
                    +Button(
                        ButtonType.CALLBACK,
                        entry,
                        if (adPlatform.tags.contains(entry)) {
                            ButtonIntent.POSITIVE
                        } else {
                            ButtonIntent.DEFAULT
                        },
                        payload = Payload(
                            TagSwitchPlatformState::class,
                            TagSwitchPlatformState(
                                timestamp,
                                chatId,
                                entry
                            ).toJson()
                        ).toJson()

                    )
                }
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