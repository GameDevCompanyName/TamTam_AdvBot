package me.evgen.advbot.storage

import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.UserId
import chat.tamtam.botsdk.model.prepared.Chat
import chat.tamtam.botsdk.model.prepared.User
import me.evgen.advbot.model.AdPlatform
import me.evgen.advbot.model.Advert
import me.evgen.advbot.model.TempAdvert
import java.util.concurrent.atomic.AtomicLong

object LocalStorage {
    private val idGenerator = AtomicLong(0L)

    private val advertsMap: MutableMap<User, MutableSet<Advert>> = mutableMapOf()
    private val chatsMap: MutableMap<UserId, MutableSet<AdPlatform>> = mutableMapOf()

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

    fun getChatById(userId: UserId, chatId: Long): Chat? {
        val adPlatform = chatsMap[userId]?.find { it.chat.chatId.id == chatId }
        return adPlatform?.chat
    }

    fun getChatNames(userId: UserId): List<String> {
        val names = mutableListOf<String>()
        if (chatsMap.containsKey(userId)) {
            for (entry in chatsMap[userId]!!) {
                names.add(entry.getChatTitle())
            }
        }
        return names
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

    private fun createAdPlatformFromChat(chat: Chat): AdPlatform {
        return AdPlatform(
            chat,
            mutableSetOf(),
            false
        )
    }
}
