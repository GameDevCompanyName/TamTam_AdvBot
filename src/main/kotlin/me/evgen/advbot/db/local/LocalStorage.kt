package me.evgen.advbot.db.local

import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.UserId
import me.evgen.advbot.model.AdPlatform
import me.evgen.advbot.model.IPlatform
import me.evgen.advbot.model.entity.TempAdvert
import me.evgen.advbot.model.entity.User
import java.util.concurrent.atomic.AtomicLong

class LocalStorage {
    private val idGenerator = AtomicLong(0L)
    private val tempAdvertsByUserId = mutableMapOf<Long, TempAdvert>()

    private val chatsMap: MutableMap<Long, MutableSet<IPlatform>> = mutableMapOf()

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

    fun addChat(platform: IPlatform) {
        if (chatsMap.containsKey(platform.user.id)) {
            chatsMap[platform.user.id]!!.add(platform)

        } else {
            val chatSet = mutableSetOf<IPlatform>().apply {
                add(platform)
            }
            chatsMap[platform.user.id] = chatSet
        }
    }

    fun removeChat(user: UserId, id: ChatId) {
        //TODO
    }

    fun getPlatforms(userId: Long): Set<IPlatform> {
        val chats = mutableSetOf<IPlatform>()
        if (chatsMap.containsKey(userId)) {
            for (entry in chatsMap[userId]!!) {
                chats.add(entry)
            }
        }
        return chats
    }

    fun getPlatform(userId: Long, chatId: Long): IPlatform? {
        return chatsMap[userId]?.find { it.id == chatId }
    }


}
