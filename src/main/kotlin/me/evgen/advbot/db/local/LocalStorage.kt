package me.evgen.advbot.db.local

import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.UserId
import chat.tamtam.botsdk.model.prepared.Chat
import me.evgen.advbot.model.AdPlatform
import me.evgen.advbot.model.entity.TempAdvert
import java.util.concurrent.atomic.AtomicLong

class LocalStorage {
    private val idGenerator = AtomicLong(0L)
    private val tempAdvertsByUserId = mutableMapOf<Long, TempAdvert>()

    private val chatsMap: MutableMap<UserId, MutableSet<AdPlatform>> = mutableMapOf()

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

    fun addChat(userId: UserId, chat: Chat) {
        if (chatsMap.containsKey(userId)) {
            chatsMap[userId]!!.add(createAdPlatformFromChat(chat))

        } else {
            val chatSet = mutableSetOf<AdPlatform>().apply {
                add(createAdPlatformFromChat(chat))
            }
            chatsMap[userId] = chatSet
        }
    }

    fun removeChat(user: UserId, id: ChatId) {
        //TODO
    }

    fun getChats(userId: UserId): Set<Chat> {
        val chats = mutableSetOf<Chat>()
        if (chatsMap.containsKey(userId)) {
            for (entry in chatsMap[userId]!!) {
                chats.add(entry.chat)
            }
        }
        return chats
    }

    fun getPlatform(userId: UserId, chatId: Long): AdPlatform? {
        return chatsMap[userId]?.find { it.chat.chatId.id == chatId }
    }

    fun tagSwitchPlatform(userId: UserId, adPlatform: AdPlatform, tag: String) {
        chatsMap[userId]?.first { it.getChatId() == adPlatform.getChatId() }?.apply {
            if (!tags.contains(tag)) {
                tags.add(tag)
            } else tags.remove(tag)
        }
    }

    private fun createAdPlatformFromChat(chat: Chat): AdPlatform {
        return AdPlatform(
            chat,
            mutableSetOf(),
            false
        )
    }
}
