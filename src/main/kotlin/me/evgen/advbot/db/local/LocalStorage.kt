package me.evgen.advbot.db.local

import chat.tamtam.botsdk.model.ChatId
import chat.tamtam.botsdk.model.UserId
import me.evgen.advbot.model.entity.IPlatform
import me.evgen.advbot.model.entity.TempAdvert
import java.util.concurrent.atomic.AtomicLong

class LocalStorage {
    private val idGenerator = AtomicLong(0L)
    private val tempAdvertsByUserId = mutableMapOf<Long, TempAdvert>()

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
}
