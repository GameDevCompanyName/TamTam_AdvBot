package me.evgen.advbot

import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonIntent
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
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.TempAdvert

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
            +getCancelButton(payload)
        }
    }
}

fun getBackButton(payload: Payload): Button {
    return Button(
        ButtonType.CALLBACK,
        "⬅ Назад",
        payload = payload.toJson()
    )
}

fun getDoneButton(payload: Payload): Button {
    return Button(
        ButtonType.CALLBACK,
        "Готово ✅",
        intent = ButtonIntent.POSITIVE,
        payload = payload.toJson()
    )
}

fun getCancelButton(payload: Payload, needNegativeIntent: Boolean = false): Button {
    return if (needNegativeIntent) {
        Button(
            ButtonType.CALLBACK,
            "❌ Отмена",
            payload = payload.toJson(),
            intent = ButtonIntent.NEGATIVE
        )
    } else {
        Button(
            ButtonType.CALLBACK,
            "❌ Отмена",
            payload = payload.toJson()
        )
    }
}
