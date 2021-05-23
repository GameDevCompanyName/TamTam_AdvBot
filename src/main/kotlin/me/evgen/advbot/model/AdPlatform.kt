package me.evgen.advbot.model

import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.prepared.Chat

data class AdPlatform(val chat: Chat, var tags: MutableSet<String>, var availability: Boolean) {

    fun getChatTitle(): String {
        return chat.title
    }

    fun getChatId(): ChatId {
        return chat.chatId
    }

    fun addTag(tag: String) {
        tags.add(tag)
    }

    fun getTagsString(): String {
        val tagString = ""
        for (entry in tags) {
            tagString + entry + "\n"
        }
        return tagString
    }

    fun getAvailability(): String {
        return if (availability) "Доступна"
        else "Недоступна"
    }

    fun accessSwitch() {
        availability = !availability
    }

    fun getAccessButton(): String {
        return if (availability) "Выключить"
        else "Включить"
    }
}
