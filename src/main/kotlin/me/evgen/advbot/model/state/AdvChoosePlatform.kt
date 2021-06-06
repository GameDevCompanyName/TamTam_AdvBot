package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.prepared.Chat
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.Payloads
import me.evgen.advbot.emoji.Emoji
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.service.PlatformService

class AdvChoosePlatform(
    timestamp: Long,
    private val advertId: Long,
    private val isForward: Boolean,
    private val anchorId: Long = -1,
    private val pageNum: Int = 1
) : BaseState(timestamp), CustomCallbackState {

    companion object {
        private const val QUANTITY_PLATFORM_ON_PAGE = 2
    }

    override suspend fun handle(callbackState: CallbackState, requestsManager: RequestsManager) {
        val platformList = PlatformService.getPlatformsForPosting(
            anchorId,
            QUANTITY_PLATFORM_ON_PAGE,
            isForward,
            advertId
        )
        val chatList = mutableListOf<Chat>()
        for (p in platformList) {
            val chat = p.getChatFromServer(requestsManager)
            if (chat != null) {
                chatList.add(chat)
            }
        }

        val keyboard = createKeyboard(
            chatList,
            if (platformList.isEmpty()) -1L else platformList.first().id,
            if (platformList.isEmpty()) -1L else platformList.last().id
        )

        """Страница №${pageNum}
        |Выберите платформу для публикации:""".trimMargin()
            .sendToUserWithKeyboard(
                callbackState.getUserId(),
                keyboard,
                requestsManager
            )
    }

    private fun createKeyboard(chatList: List<Chat>, firstAnchorId: Long, lastAnchorId: Long): InlineKeyboard {
        return keyboard {
            for (chat in chatList) {
                +buttonRow {
                    +Button(
                        ButtonType.CALLBACK,
                        "${chat.title} (${chat.participantsCount} ₽)",
                        payload = Payloads.WIP
                    )
                }
            }

            +buttonRow {
                if (pageNum > 1 && firstAnchorId != -1L) {
                    +Button(
                        ButtonType.CALLBACK,
                        "${Emoji.PREVIOUS_PAGE} Страница №${pageNum - 1}",
                        payload = AdvChoosePlatform(
                            timestamp,
                            advertId,
                            false,
                            firstAnchorId,
                            pageNum - 1
                        ).toPayload().toJson()
                    )
                } else {
                    +CallbackButton.EMPTY.create(Payloads.EMPTY)
                }
                +CallbackButton.DEFAULT_CANCEL.create(
                    AdvState(timestamp, advertId).toPayload()
                )
                if (chatList.size == QUANTITY_PLATFORM_ON_PAGE && lastAnchorId != -1L) {
                    +Button(
                        ButtonType.CALLBACK,
                        "Страница №${pageNum + 1} ${Emoji.NEXT_PAGE}",
                        payload = AdvChoosePlatform(
                            timestamp,
                            advertId,
                            true,
                            lastAnchorId,
                            pageNum + 1
                        ).toPayload().toJson()
                    )
                } else {
                    +CallbackButton.EMPTY.create(Payloads.EMPTY)
                }
            }
        }
    }
}
