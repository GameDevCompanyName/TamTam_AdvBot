package me.evgen.advbot.model.entity

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.prepared.Chat
import me.evgen.advbot.db.TableName
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = TableName.PLATFORM)
class Platform() : IPlatform {

    @Id
    override var id: Long = 0
    override var availability: Boolean = false
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "${TableName.USER}_id")
    override lateinit var user: User

    //TODO create many to many table for platform-tags
    @Transient
    override var tags: MutableSet<String> = mutableSetOf()

    constructor(id: Long,
                availability: Boolean,
                user: User) : this() {

        this.id = id
        this.availability = availability
        this.user = user
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

    override fun getAccessButton(): String {
        return if (availability) "Выключить"
        else "Включить"
    }

    override suspend fun getChatFromServer(requestsManager: RequestsManager): Chat? {
        when (val res = requestsManager.getChat(ChatId(id))) {
            is ResultRequest.Success -> {
                return res.response
            }
        }
        return null
    }
}
