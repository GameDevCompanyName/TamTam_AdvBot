package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.TempAdvert

class TempAdvertDaoImpl : TempAdvertDao<TempAdvert>() {
    override fun findByUserId(userId: Long): TempAdvert? {
        return getLocalStorage().findTempAdvertByUserId(userId)
    }

    override fun insert(entity: TempAdvert) {
        getLocalStorage().addTempAdvert(entity)
    }

    override fun update(entity: TempAdvert) {
        getLocalStorage().updateTempAdvert(entity)
    }

    override fun deleteTempAdvert(userId: Long) {
        getLocalStorage().deleteTempAdvertByUserId(userId)
    }
}
