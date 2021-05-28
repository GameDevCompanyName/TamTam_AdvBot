package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.model.AttachType
import chat.tamtam.botsdk.model.CallbackId
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.UserId
import chat.tamtam.botsdk.model.request.AnswerCallback
import chat.tamtam.botsdk.model.request.AttachmentKeyboard
import chat.tamtam.botsdk.model.request.EMPTY_INLINE_KEYBOARD
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.model.request.SendMessage
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.CommandState
import chat.tamtam.botsdk.state.MessageState
import chat.tamtam.botsdk.state.StartedBotState
import com.google.gson.Gson
import me.evgen.advbot.getUserId
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.service.AdvertService

abstract class BaseState(var timestamp: Long) {

    // Если тебе вдруг понадобился этот метод, то сначала подумой и узнай у Алексея Корнеева че делать.
    // А вообщего его нужно удалить, но мне лень)
    fun getOrDefaultPayload(state: BaseState?): Payload {
        return if (state == null) {
            Payload(
                StartState::class,
                StartState(System.currentTimeMillis()).toJson()
            )
        } else {
            Payload(
                state::class,
                state.apply { timestamp = this@BaseState.timestamp }.toJson()
            )
        }
    }

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

    suspend fun String.answerWithKeyboard(callbackId: CallbackId, inlineKeyboard: InlineKeyboard, requestsManager: RequestsManager) {
        val message = SendMessage(this, listOf(AttachmentKeyboard(AttachType.INLINE_KEYBOARD.value.toLowerCase(), inlineKeyboard)))
        val answerCallback = AnswerCallback(message)

        requestsManager.answer(callbackId, answerCallback)
    }

    suspend fun String.answerNotification(userId: UserId, callbackId: CallbackId, requestsManager: RequestsManager) {
        val answerCallback = AnswerCallback(userId = userId.id, notification = this)
        requestsManager.answer(callbackId, answerCallback)
    }
}

interface CustomCommandState {
    suspend fun handle(commandState: CommandState, prevState: BaseState, requestsManager: RequestsManager) {
        AdvertService.deleteTempAdvertByUserId(commandState.getUserId().id)
    }
}

interface CustomCallbackState {
    suspend fun handle(callbackState: CallbackState, prevState: BaseState, requestsManager: RequestsManager)
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
