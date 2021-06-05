package me.evgen.advbot.service

import me.evgen.advbot.db.dao.AdvertDaoImpl
import me.evgen.advbot.model.entity.Advert

object AdvertService {

    private val advertDao = AdvertDaoImpl()

    fun findAdvert(id: Long): Advert? {
        return advertDao.findAdvert(id)
    }

    fun addAdvert(advert: Advert): Long? {
        val result = advertDao.insert(advert)
        return if (result is Long) {
            result
        } else {
            null
        }
    }

    fun updateAdvert(advert: Advert) {
        advertDao.update(advert)
    }

    fun deleteAdvert(advertId: Long) {
        advertDao.deleteAdvert(advertId)
    }

    fun findAdverts(userId: Long): List<Advert> {
        return advertDao.findAdverts(userId)
    }
}
