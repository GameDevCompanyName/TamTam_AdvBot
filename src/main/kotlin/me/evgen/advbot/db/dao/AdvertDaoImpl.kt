package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.Advert

class AdvertDaoImpl : AdvertDao<Advert>() {
    override fun findAdvert(id: Long): Advert? {
        return find(id)
    }

    override fun findAdverts(userId: Long): List<Advert> {
        return findAllByColumnName<Advert>("owner", userId).toList()
    }

    override fun deleteAdvert(id: Long) {
        delete<Advert>(id)
    }
}
