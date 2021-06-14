package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.model.AttachType
import chat.tamtam.botsdk.model.CallbackId
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.UserId
import chat.tamtam.botsdk.model.request.*
import chat.tamtam.botsdk.model.response.LinkType
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.CommandState
import chat.tamtam.botsdk.state.MessageState
import chat.tamtam.botsdk.state.StartedBotState
import com.google.gson.Gson
import me.evgen.advbot.model.navigation.Payload

abstract class BaseState(var timestamp: Long) {
    private val techChannelId = -78551009460407

    fun toPayload(): Payload {
        return Payload(
            this::class,
            toJson()
        )
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    suspend fun String.sendTo(userId: UserId, requestsManager: RequestsManager) = requestsManager.send(userId, SendMessage(this))

    suspend fun String.sendTo(chatId: ChatId, requestsManager: RequestsManager) = requestsManager.send(chatId, SendMessage(this))

    suspend fun String.sendToUserWithKeyboard(userId: UserId, inlineKeyboard: InlineKeyboard, requestsManager: RequestsManager) {
        val attaches = if (inlineKeyboard == EMPTY_INLINE_KEYBOARD) {
            emptyList()
        } else {
            listOf(AttachmentKeyboard(AttachType.INLINE_KEYBOARD.value.toLowerCase(), inlineKeyboard))
        }
        requestsManager.send(userId, SendMessage(this, attaches))
    }

    suspend fun String.sendToUserWithKeyboardAndAttachments(
        userId: UserId,
        inlineKeyboard: InlineKeyboard,
        attachments: List<AttachmentContract>,
        requestsManager: RequestsManager) {

        val attaches = mutableListOf<AttachmentContract>()
        if (inlineKeyboard != EMPTY_INLINE_KEYBOARD) {
            attaches.add(AttachmentKeyboard(AttachType.INLINE_KEYBOARD.value.toLowerCase(), inlineKeyboard))
        }
        attaches.addAll(attachments)
        requestsManager.send(userId, SendMessage(this, attaches))
    }

    suspend fun String.answerWithKeyboard(callbackId: CallbackId, inlineKeyboard: InlineKeyboard, requestsManager: RequestsManager) {
        val message = SendMessage(this, listOf(AttachmentKeyboard(AttachType.INLINE_KEYBOARD.value.toLowerCase(), inlineKeyboard)))
        val answerCallback = AnswerCallback(message)

        requestsManager.answer(callbackId, answerCallback)
    }

    suspend fun String.answerNotification(userId: UserId, callbackId: CallbackId, requestsManager: RequestsManager) {
        val answerCallback = AnswerCallback(userId = userId.id, notification = this)
        requestsManager.answer(callbackId, answerCallback)
    }

    suspend fun String.sendThroughTech(chatId: ChatId, attachment: AttachmentContract?, requestsManager: RequestsManager) {
        val message: SendMessage
        val attachmentList : List<AttachmentContract>
        if (attachment != null) {
            attachmentList = listOf(attachment)
            message = SendMessage(this, attachmentList)
        } else {
            message = SendMessage(this)
        }
        when (val res = requestsManager.send(ChatId(techChannelId), message)) {
            is ResultRequest.Success -> {
                requestsManager.send(chatId, SendMessage("", emptyList(), true, LinkOnMessage(LinkType.FORWARD, res.response.body.messageId)))
            }
        }
    }
}

interface CustomCommandState {
    suspend fun handle(
        commandState: CommandState,
        requestsManager: RequestsManager
    )
}

interface CustomCallbackState {
    suspend fun handle(
        callbackState: CallbackState,
        requestsManager: RequestsManager
    )
}

interface CustomMessageState {
    suspend fun handle(
        messageState: MessageState,
        requestsManager: RequestsManager
    )
}

interface CustomStartedBotState {
    suspend fun handle(startedBotState: StartedBotState, requestsManager: RequestsManager)
}

interface MessageListener {
    suspend fun onMessageReceived(messageState: MessageState, requestsManager: RequestsManager)
}
