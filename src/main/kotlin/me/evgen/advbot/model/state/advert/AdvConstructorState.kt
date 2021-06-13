package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.MessageState
import me.evgen.advbot.Payloads
import me.evgen.advbot.emoji.Emoji
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.ErrorType
import me.evgen.advbot.model.entity.Tag
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.model.state.CustomMessageState
import me.evgen.advbot.service.AdvertService

class AdvConstructorState(
    timestamp: Long,
    private val advertId: Long,
    private val isCreatingAdvert: Boolean
) : BaseState(timestamp), CustomCallbackState, CustomMessageState {

    override suspend fun handle(callbackState: CallbackState, requestsManager: RequestsManager) {
        var message = ErrorType.EDIT_ADVERT.errorMessage
        if (!isCreatingAdvert) {
            val advert = AdvertService.findAdvert(advertId)
            if (advert != null) {
                message = createMessage(advert.title, advert.text, advert.tags)
            }
        }

        val keyboard = if (message == ErrorType.EDIT_ADVERT.errorMessage) {
            if (isCreatingAdvert) {
                createErrorKeyboard(MenuAdvertState(timestamp).toPayload())
            } else {
                createErrorKeyboard(AdvState(timestamp, advertId).toPayload())
            }
        } else {
            createKeyboard()
        }

        message.answerWithKeyboard(callbackState.callback.callbackId, keyboard, requestsManager)
    }

    override suspend fun handle(messageState: MessageState, requestsManager: RequestsManager) {
        val advert = AdvertService.findAdvert(advertId)
        val message: String
        val keyboard: InlineKeyboard
        if (advert != null) {
            message = createMessage(advert.title, advert.text, advert.tags)
            keyboard = createKeyboard()
        } else {
            message = ErrorType.EDIT_ADVERT.errorMessage
            keyboard = keyboard {
                +buttonRow {
                    +CallbackButton.RELOAD.create(
                        AdvConstructorState(
                            timestamp,
                            advertId,
                            isCreatingAdvert
                        ).toPayload()
                    )
                }
            }
        }

        message.sendToUserWithKeyboard(messageState.getUserId(), keyboard, requestsManager)
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка названия ${Emoji.PENCIL}",
                    payload = Payload(
                        AdvTitlingState::class,
                        AdvTitlingState(
                            timestamp,
                            advertId,
                            isCreatingAdvert
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка текста ${Emoji.TEXT}",
                    payload = Payload(
                        AdvTextingState::class,
                        AdvTextingState(
                            timestamp,
                            advertId,
                            isCreatingAdvert
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка изображения 📺",
                    payload = Payloads.WIP
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настройка тегов ${Emoji.TAG}",
                    payload = Payload(
                        TagSelectionAdvertState::class,
                        TagSelectionAdvertState(
                            timestamp, advertId
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +CallbackButton.TO_ACTIONS.create(
                    Payload(
                        AdvState::class, AdvState(
                            timestamp,
                            advertId
                        ).toJson()
                    )
                )
            }
        }
    }

    private fun createErrorKeyboard(backPayload: Payload): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +CallbackButton.BACK.create(backPayload)
            }
        }
    }

    private fun createMessage(title: String, text: String, tags: Set<Tag>): String {
        val tempTitle = """Текущее название рекламы:
        |$title""".trimMargin()
        val tempText = if (text.isBlank()) {
            ""
        } else {
            """Текущий текст рекламы:
        |$text""".trimMargin()
        }
        val tagsText = if (tags.isEmpty()) {
            "Тегов нет."
        } else {
            """Тэги:
            |${tags.joinToString("\n|") { it.name }}
            """.trimMargin()
        }

        return """$tempTitle
            |
        |$tempText
        |
        |$tagsText""".trimMargin()
    }
}
