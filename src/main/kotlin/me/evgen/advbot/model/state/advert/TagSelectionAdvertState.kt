package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonIntent
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.entity.Advert
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.AdvertService
import me.evgen.advbot.service.TagService

class TagSelectionAdvertState(timestamp: Long, private val advertId: Long) : BaseState(timestamp),
    CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val advert = AdvertService.findAdvert(advertId)
        if (advert == null) {
            "Ошибка! Нет такого объявления.".answerNotification(callbackState.getUserId(), callbackState.callback.callbackId, requestsManager)
            return
        }

        """Настройка тегов для объявления:
            |${advert.title}
            | 
            |Чтобы добавить или удалить тег, нажмите на соответствующую кнопку.
            |""".trimMargin().answerWithKeyboard(
            callbackState.callback.callbackId,
            createKeyboard(advert),
            requestsManager
        )
    }

    private fun createKeyboard(advert: Advert): InlineKeyboard {
        return keyboard {
            for (entry in TagService.getAllTags()) {
                +buttonRow {
                    +Button(
                        ButtonType.CALLBACK,
                        entry.name,
                        if (advert.tags.contains(entry)) {
                            ButtonIntent.POSITIVE
                        } else {
                            ButtonIntent.DEFAULT
                        },
                        payload = Payload(
                            TagSwitchAdvertState::class,
                            TagSwitchAdvertState(
                                timestamp,
                                advertId,
                                entry.id
                            ).toJson()
                        ).toJson()

                    )
                }
            }
            +buttonRow {
                +getBackButton(
                    Payload(
                        AdvConstructorState::class, AdvConstructorState(
                            timestamp,
                            advertId,
                            false
                        ).toJson()
                    )
                )
            }
        }
    }
}