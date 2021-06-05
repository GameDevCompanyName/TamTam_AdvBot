package me.evgen.advbot.model

import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonIntent
import chat.tamtam.botsdk.model.ButtonType
import me.evgen.advbot.model.navigation.Payload

enum class CallbackButton(private val title: String, private val intent: ButtonIntent = ButtonIntent.DEFAULT) {
    TO_ACTIONS("К действиям"),
    BACK("⬅ Назад"),
    DEFAULT_CANCEL("❌ Отмена"),
    RELOAD("Обновить");

    fun create(payload: Payload): Button {
        return Button(
            ButtonType.CALLBACK,
            title,
            payload = payload.toJson(),
            intent = intent
        )
    }
}
