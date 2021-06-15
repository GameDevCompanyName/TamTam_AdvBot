package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonIntent
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.Payloads
import me.evgen.advbot.getBackButton
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.service.AdvertService
import me.evgen.advbot.service.PlatformsForPostingArgs

class AdvState(timestamp: Long, private val advertId: Long) : BaseState(timestamp),
    CustomCallbackState {
    override suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    ) {
        val advert = AdvertService.findAdvert(advertId)
        if (advert == null) {
            "Ошибка! Нет такой рекламы.".answerNotification(
                callbackState.getUserId(),
                callbackState.callback.callbackId,
                requestsManager
            )
            return
        }

        val campaigns = AdvertService.getAllCampaignsByAd(advertId)
        val postCount = campaigns.size
        var viewCount = 0
        for (campaign in campaigns) {
            val message = campaign.getPostFromServer(requestsManager)
            if (message != null) {
                viewCount += message.statistics.views - 1 //TODO это костыль на счетчик, потому что бот считает свой просмотр (походу)
            }
        }


        """Работа с рекламой:
            | ${advert.title}
            | 
            | Данная реклама была запущена $postCount раз.
            | Общее количество просмотров рекламы: $viewCount""".trimMargin().sendToUserWithKeyboard(
            callbackState.callback.user.userId,
            createKeyboard(),
            requestsManager
        )
    }

    private fun createKeyboard(): InlineKeyboard {
        return keyboard {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Настроить рекламу ⚙",
                    payload = Payload(
                        AdvConstructorState::class,
                        AdvConstructorState(
                            timestamp,
                            advertId,
                            isCreatingAdvert = false
                        ).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Запустить рекламу 🚀",
                    intent = ButtonIntent.POSITIVE,
                    payload = AdvChoosePlatform(
                        timestamp,
                        PlatformsForPostingArgs(advertId)
                    ).toPayload().toJson()
                )
            }
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    "Удалить рекламу 🗑",
                    intent = ButtonIntent.NEGATIVE,
                    payload = Payload(
                        AdvDeleteDialogState::class,
                        AdvDeleteDialogState(timestamp, advertId).toJson()
                    ).toJson()
                )
            }
            +buttonRow {
                +getBackButton(
                    Payload(
                        AdvListState::class, AdvListState(
                            timestamp
                        ).toJson()
                    )
                )
            }
        }
    }
}