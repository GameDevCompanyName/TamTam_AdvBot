package me.evgen.advbot.model.entity

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.client.ResultRequest
import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.prepared.Chat
import me.evgen.advbot.db.TableName
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = TableName.PLATFORM_TAG,
        joinColumns = [JoinColumn(name = "${TableName.PLATFORM}_id")],
        inverseJoinColumns = [JoinColumn(name = "${TableName.TAG}_id")]
    )
    override lateinit var tags: MutableSet<Tag>

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
                append("${entry.name}\n")
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
