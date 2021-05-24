package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.Advert

interface AdvertDao : Dao {
    fun findById(id: Long): Advert?
    fun insert(advert: Advert)
    fun update(advert: Advert)
    fun delete(id: Long)
}
