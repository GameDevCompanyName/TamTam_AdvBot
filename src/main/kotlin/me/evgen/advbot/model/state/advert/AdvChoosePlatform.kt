package me.evgen.advbot.model.state.advert

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.prepared.Chat
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.model.response.Permissions
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.Payloads
import me.evgen.advbot.emoji.Emoji
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.model.state.CustomCallbackState
import me.evgen.advbot.model.state.PayPostingState
import me.evgen.advbot.service.PlatformService
import me.evgen.advbot.service.PlatformsForPostingArgs

class AdvChoosePlatform(
    timestamp: Long,
    private val args: PlatformsForPostingArgs
) : BaseState(timestamp), CustomCallbackState {

    override suspend fun handle(callbackState: CallbackState, requestsManager: RequestsManager) {
        var isPlatformListEmpty = false
        val chatList: MutableList<Chat> = mutableListOf()
        var tempAnchorId: Long = args.anchorPlatformId
        var tempQuantity = args.quantity
        do {
            val platformList = PlatformService.getPlatformsForPosting(
                tempAnchorId,
                tempQuantity,
                args.isForward,
                args.advertId
            )
            for (p in platformList) {
                val chat = p.getChatFromServer(requestsManager)
                if (chat != null) {
                    val chatMember = requestsManager.getMembershipInfoInChat(chat.chatId)
                    if (chatMember is ResultRequest.Success) {
                        val permissions =  chatMember.response.permissions
                        if (permissions != null && permissions.contains(Permissions.WRITE)) {
                            if (args.isForward || tempQuantity == args.quantity) {
                                chatList.add(chat)
                            } else {
                                chatList.add(0, chat)
                            }
                        }
                    }
                }
            }
            if (platformList.isNotEmpty()) {
                tempAnchorId = if (args.isForward) {
                    platformList.last().id
                } else {
                    platformList.first().id
                }
            } else {
                isPlatformListEmpty = true
            }
            tempQuantity = args.quantity - chatList.size
        } while (chatList.size != args.quantity && platformList.isNotEmpty())

        val hasMoreForward = !isPlatformListEmpty && (chatList.size < args.quantity
                || (chatList.isNotEmpty()
                    && PlatformService.hasMorePlatformsForPosting(chatList.last().chatId.id, 1, true, args.advertId)))

        val keyboard = createKeyboard(
            chatList,
            if (chatList.isEmpty()) -1L else chatList.first().chatId.id,
            if (chatList.isEmpty()) -1L else chatList.last().chatId.id,
            hasMoreForward
        )

        """Страница №${args.pageNum}
        |Выберите платформу для публикации:""".trimMargin()
            .sendToUserWithKeyboard(
                callbackState.getUserId(),
                keyboard,
                requestsManager
            )
    }

    private fun createKeyboard(
        chatList: List<Chat>,
        firstAnchorPlatformId: Long,
        lastAnchorPlatformId: Long,
        hasMoreForward: Boolean): InlineKeyboard {

        return keyboard {
            for (chat in chatList) {
                +buttonRow {
                    +Button(
                        ButtonType.CALLBACK,
                        "${chat.title} (${chat.participantsCount} ₽)",
                        payload = PayPostingState(
                            timestamp,
                            args.advertId,
                            chat.chatId.id,
                            chat.participantsCount
                        ).toPayload().toJson()
                    )
                }
            }

            +buttonRow {
                if (args.pageNum > 1 && firstAnchorPlatformId != -1L) {
                    +Button(
                        ButtonType.CALLBACK,
                        "${Emoji.PREVIOUS_PAGE} Страница №${args.pageNum - 1}",
                        payload = AdvChoosePlatform(
                            timestamp,
                            args.previous(firstAnchorPlatformId)
                        ).toPayload().toJson()
                    )
                } else {
                    +CallbackButton.EMPTY.create(Payloads.EMPTY)
                }
                +CallbackButton.DEFAULT_CANCEL.create(
                    AdvState(timestamp, args.advertId).toPayload()
                )
                if (hasMoreForward && lastAnchorPlatformId != -1L) {
                    +Button(
                        ButtonType.CALLBACK,
                        "Страница №${args.pageNum + 1} ${Emoji.NEXT_PAGE}",
                        payload = AdvChoosePlatform(
                            timestamp,
                            args.next(lastAnchorPlatformId)
                        ).toPayload().toJson()
                    )
                } else {
                    +CallbackButton.EMPTY.create(Payloads.EMPTY)
                }
            }
        }
    }
}
