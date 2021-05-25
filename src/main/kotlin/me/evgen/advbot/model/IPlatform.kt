package me.evgen.advbot.model

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.model.prepared.Chat
import me.evgen.advbot.model.entity.User


interface IPlatform {

    val id: Long
    var tags: MutableSet<String>
    var availability: Boolean
    val user: User

    suspend fun getChatFromServer(requestsManager: RequestsManager): Chat?

    fun getTagsString(): String

    fun getAvailability(): String

    fun accessSwitch()

    fun getAccessButton(): String

}