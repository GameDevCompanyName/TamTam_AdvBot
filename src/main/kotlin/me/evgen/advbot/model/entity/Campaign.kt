package me.evgen.advbot.model.entity

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.MessageId
import chat.tamtam.botsdk.model.prepared.Message

class Campaign : ICampaign {

    private val techChannelId = -78551009460407

    override var postId: String = "0"
    override var adId: Long = 0

    override suspend fun getPostFromServer(requestsManager: RequestsManager): Message? {
        when (val res = requestsManager.getAllMessages(ChatId(techChannelId), listOf(MessageId(postId)))) {
            is ResultRequest.Success -> {
                return res.response.first()
            }
        }
        return null
    }
}