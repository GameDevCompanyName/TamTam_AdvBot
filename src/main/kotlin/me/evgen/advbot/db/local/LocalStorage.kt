package me.evgen.advbot.db.local

import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.UserId
import me.evgen.advbot.model.entity.TempAdvert
import java.util.concurrent.atomic.AtomicLong

class LocalStorage {
    private val idGenerator = AtomicLong(0L)
    private val tempAdvertsByUserId = mutableMapOf<Long, TempAdvert>()

    //TODO: сделать мапу по Chat вместо ChatId
    private val chatNamesMap: MutableMap<ChatId, String> = mutableMapOf()
    private val userChatsMap: MutableMap<UserId , MutableSet<ChatId>> = mutableMapOf()

    fun getNextId(): Long = idGenerator.getAndIncrement()

    fun findTempAdvertByUserId(userId: Long): TempAdvert? {
        return tempAdvertsByUserId[userId]
    }

    fun addTempAdvert(tempAdvert: TempAdvert) {
        tempAdvertsByUserId[tempAdvert.owner.id] = tempAdvert
    }

    fun updateTempAdvert(tempAdvert: TempAdvert) {
        tempAdvertsByUserId[tempAdvert.owner.id] = tempAdvert
    }

    fun deleteTempAdvertByUserId(userId: Long) {
        tempAdvertsByUserId.remove(userId)
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

    fun addChat(userId: UserId, id: ChatId) {
        if (userChatsMap.containsKey(userId)) {
            userChatsMap[userId]!!.add(id)
        } else {
            val chatSet = mutableSetOf<ChatId>().apply {
                add(id)
            }
            userChatsMap[userId] = chatSet
        }
    }

    fun removeChat(user: UserId, id: ChatId) {
        userChatsMap[user]?.removeIf { it.id == id.id }
    }

    fun getChats(user: UserId): Set<ChatId> {
        return userChatsMap[user] ?: setOf()
    }
}
