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
import me.evgen.advbot.model.Tags
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
            |${adPlatform.getChatTitle()}
            | 
            |Для добавления тега нажмите на соответствующую кнопку.
            | 
            |Список текущих тегов:
            |${adPlatform.getTagsString()}""".trimMargin().answerWithKeyboard(
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
                    payload = Payload(
                        TagSwitchPlatformState::class,
                        TagSwitchPlatformState(
                            timestamp,
                            chatId,
                            Tags.SPORTS
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Новости",
                    payload = Payload(
                        TagSwitchPlatformState::class,
                        TagSwitchPlatformState(
                            timestamp,
                            chatId,
                            Tags.NEWS
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Игры",
                    payload = Payload(
                        TagSwitchPlatformState::class,
                        TagSwitchPlatformState(
                            timestamp,
                            chatId,
                            Tags.GAMES
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Политика",
                    payload = Payload(
                        TagSwitchPlatformState::class,
                        TagSwitchPlatformState(
                            timestamp,
                            chatId,
                            Tags.POLITICS
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Технологии",
                    payload = Payload(
                        TagSwitchPlatformState::class,
                        TagSwitchPlatformState(
                            timestamp,
                            chatId,
                            Tags.TECH
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Экономика",
                    payload = Payload(
                        TagSwitchPlatformState::class,
                        TagSwitchPlatformState(
                            timestamp,
                            chatId,
                            Tags.ECONOMICS
                        ).toJson()
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