package me.evgen.advbot.storage

import chat.tamtam.botsdk.model.prepared.User
import me.evgen.advbot.model.Advert
import me.evgen.advbot.model.TempAdvert
import java.util.concurrent.atomic.AtomicLong

object LocalStorage {
    private val idGenerator = AtomicLong(0L)

    private val advertsMap: MutableMap<User, MutableSet<Advert>> = mutableMapOf()

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
