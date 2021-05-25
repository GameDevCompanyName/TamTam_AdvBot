package me.evgen.advbot.model

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.prepared.Chat
import me.evgen.advbot.model.entity.User

data class AdPlatform(override val id: Long, override var tags: MutableSet<String>, override var availability: Boolean,
                      override val user: User
) : IPlatform {

    override suspend fun getChatFromServer(requestsManager: RequestsManager): Chat? {
        when (val res = requestsManager.getChat(ChatId(id))) {
            is ResultRequest.Success -> {
                return res.response
            }
        }
        return null
    }

    override fun getTagsString(): String {
        return buildString {
            for (entry in tags) {
                append("$entry\n")
            }
        }
    }

    override fun getAvailability(): String {
        return if (availability) "Доступна"
        else "Недоступна"
    }

    override fun accessSwitch() {
        availability = !availability
    }

    override fun getAccessButton(): String {
        return if (availability) "Выключить"
        else "Включить"
    }
}
