package me.evgen.advbot.service

import me.evgen.advbot.db.dao.AdvertDaoImpl
import me.evgen.advbot.db.dao.CampaignDaoImpl
import me.evgen.advbot.db.dao.TagsDaoImpl
import me.evgen.advbot.model.entity.Advert
import me.evgen.advbot.model.entity.Campaign

object AdvertService {

    private val advertDao = AdvertDaoImpl()
    private val tagDao = TagsDaoImpl()
    private val campaignDao = CampaignDaoImpl()

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

    fun tagSwitchAdvert(advert: Advert, tagId: Long) {
        val tag = tagDao.findTag(tagId)
        if (tag != null) {
            advert.apply {
                if (!tags.contains(tag)) {
                    tags.add(tag)
                } else tags.remove(tag)
            }
            advertDao.update(advert)
        }
    }

    fun addPost(campaign: Campaign) {
        campaignDao.update(campaign)
    }
}
