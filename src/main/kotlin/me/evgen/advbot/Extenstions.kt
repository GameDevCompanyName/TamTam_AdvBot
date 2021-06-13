package me.evgen.advbot

import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.UserId
import chat.tamtam.botsdk.model.prepared.User
import chat.tamtam.botsdk.model.request.InlineKeyboard
import chat.tamtam.botsdk.state.AddedBotState
import chat.tamtam.botsdk.state.AddedUserState
import chat.tamtam.botsdk.state.CallbackState
import chat.tamtam.botsdk.state.CommandState
import chat.tamtam.botsdk.state.MessageState
import chat.tamtam.botsdk.state.RemovedBotState
import chat.tamtam.botsdk.state.RemovedUserState
import chat.tamtam.botsdk.state.StartedBotState
import chat.tamtam.botsdk.state.UpdateState
import me.evgen.advbot.model.CallbackButton
import me.evgen.advbot.model.navigation.Payload

fun UpdateState.getUserId(): UserId {
    return when (this) {
        is CommandState -> this.command.message.sender.userId
        is CallbackState -> this.callback.user.userId
        is MessageState -> this.message.sender.userId
        is StartedBotState -> this.user.userId
        is AddedBotState -> this.user.userId
        is RemovedBotState -> this.user.userId
        is AddedUserState -> this.user.userId
        is RemovedUserState -> this.user.userId
    }
}

fun UpdateState.getUserIdLong(): Long {
    return when (this) {
        is CommandState -> this.command.message.sender.userId.id
        is CallbackState -> this.callback.user.userId.id
        is MessageState -> this.message.sender.userId.id
        is StartedBotState -> this.user.userId.id
        is AddedBotState -> this.user.userId.id
        is RemovedBotState -> this.user.userId.id
        is AddedUserState -> this.user.userId.id
        is RemovedUserState -> this.user.userId.id
    }
}

fun UpdateState.getUser(): User {
    return when(this) {
        is StartedBotState -> this.user
        is AddedBotState -> this.user
        is RemovedBotState -> this.user
        is AddedUserState -> this.user
        is RemovedUserState -> this.user
        is CallbackState -> this.callback.user
        is MessageState -> this.message.sender
        is CommandState -> this.command.message.sender
    }
}

fun createCancelKeyboard(payload: Payload): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +CallbackButton.DEFAULT_CANCEL.create(payload)
        }
    }
}

@Deprecated(message = "Use me.evgen.advbot.model.CallbackButton.BACK")
fun getBackButton(payload: Payload): Button {
    return getButton("⬅ Назад", payload)
}

private fun getButton(title: String, payload: Payload): Button {
    return Button(
        ButtonType.CALLBACK,
        title,
        payload = payload.toJson()
    )
}
