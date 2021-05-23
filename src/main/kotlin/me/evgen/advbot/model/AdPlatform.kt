package me.evgen.advbot.model

import chat.tamtam.botsdk.model.prepared.Chat

data class AdPlatform(val chat: Chat, var tags: MutableSet<String>) {

    fun getChatTitle(): String {
        return chat.title
    }
}
