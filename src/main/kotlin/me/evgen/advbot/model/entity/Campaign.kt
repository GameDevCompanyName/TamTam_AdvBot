package me.evgen.advbot.model.entity

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.MessageId
import chat.tamtam.botsdk.model.prepared.Message
import me.evgen.advbot.TECH_CHANNEL_ID
import me.evgen.advbot.db.TableName
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = TableName.CAMPAIGN)
class Campaign() : ICampaign {

    @Id
    override var postId: String = "0"
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "${TableName.ADVERT}_id")
    override lateinit var advert: Advert

    constructor(postId: String, advert: Advert) : this() {
        this.postId = postId
        this.advert = advert
    }

    override suspend fun getPostFromServer(requestsManager: RequestsManager): Message? {
        when (val res = requestsManager.getAllMessages(ChatId(TECH_CHANNEL_ID), listOf(MessageId(postId)), count = 100)) {
            is ResultRequest.Success -> {
                for (message in res.response) {
                    if (message.body.messageId.id == postId) {
                        return message
                    }
                }
            }
        }
        return null
    }
}