package me.evgen.advbot.storage

import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.prepared.User
import me.evgen.advbot.model.Advert
import me.evgen.advbot.model.TempAdvert
import java.util.concurrent.atomic.AtomicLong

object LocalStorage {
    private val idGenerator = AtomicLong(0L)

    private val advertsMap: MutableMap<User, MutableSet<Advert>> = mutableMapOf()
    private val chatNamesMap: MutableMap<ChatId, String> = mutableMapOf()
    private val userChatsMap: MutableMap<User, MutableSet<ChatId>> = mutableMapOf()

    fun addAdvert(user: User, tempAdvert: TempAdvert) {
        if (advertsMap.containsKey(user)) {
            advertsMap[user]!!.add(createAdvertFromTemp(tempAdvert))
        } else {
            val advertSet = mutableSetOf<Advert>().apply {
                add(createAdvertFromTemp(tempAdvert))
            }
            advertsMap[user] = advertSet
        }
    }

    fun addChatName(id: ChatId, name: String) {
        chatNamesMap[id] = name
    }

    fun getChatName(id: ChatId): String {
        return if (chatNamesMap.containsKey(id)) {
            chatNamesMap[id]!!
        } else {
            "Not found"
        }
    }

    fun addChat(user: User, id: ChatId) {
        if (userChatsMap.containsKey(user)) {
            userChatsMap[user]!!.add(id)
        } else {
            val chatSet = mutableSetOf<ChatId>().apply {
                add(id)
            }
            userChatsMap[user] = chatSet
        }
    }

    fun removeChat(user: User, id: ChatId) {
        userChatsMap[user]?.removeIf { it.id == id.id }
    }

    fun getChats(user: User): Set<ChatId> {
        return userChatsMap[user] ?: setOf()
    }

    fun updateAdvert(user: User, advertId: Long, tempAdvert: TempAdvert) {
        advertsMap[user]?.first { it.id == advertId }?.apply {
            title = tempAdvert.title
            text = tempAdvert.text
        }
    }

    fun deleteAdvert(user: User, advertId: Long) {
        advertsMap[user]?.removeIf { it.id == advertId }
    }

    fun getAds(user: User): Set<Advert> {
        return advertsMap[user] ?: setOf()
    }

    fun getAd(user: User, adId: Long): Advert? {
        return advertsMap[user]?.find { it.id == adId }
    }

    private fun createAdvertFromTemp(tempAdvert: TempAdvert): Advert {
        return Advert(
            idGenerator.getAndIncrement(),
            tempAdvert.title,
            tempAdvert.text
        )
    }
}
