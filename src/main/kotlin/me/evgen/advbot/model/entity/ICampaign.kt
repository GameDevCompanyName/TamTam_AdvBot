package me.evgen.advbot.model.entity

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.model.prepared.Message

interface ICampaign {

    var postId: String
    var adId: Long

    suspend fun getPostFromServer(requestsManager: RequestsManager): Message?
}