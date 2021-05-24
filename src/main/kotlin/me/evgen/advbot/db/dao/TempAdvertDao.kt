package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.TempAdvert

interface TempAdvertDao : Dao {
    fun findByUserId(userId: Long): TempAdvert?
    fun insert(tempAdvert: TempAdvert)
    fun update(tempAdvert: TempAdvert)
    fun delete(userId: Long)
}
