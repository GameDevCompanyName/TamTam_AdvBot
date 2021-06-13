package me.evgen.advbot.model.entity

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.model.prepared.Chat


interface IPlatform {

    var id: Long
    var tags: MutableSet<Tag>
    var availability: Boolean
    var user: User

    suspend fun getChatFromServer(requestsManager: RequestsManager): Chat?

    fun getTagsString(): String

    fun getAvailability(): String

    fun getAccessButton(): String
}
