package me.evgen.advbot.db.dao

import me.evgen.advbot.db.DBSessionFactoryUtil
import me.evgen.advbot.model.entity.TempAdvert

class TempAdvertDaoImpl : TempAdvertDao {
    override fun findByUserId(userId: Long): TempAdvert? {
        return DBSessionFactoryUtil.localStorage.findTempAdvertByUserId(userId)
    }

    override fun insert(tempAdvert: TempAdvert) {
        DBSessionFactoryUtil.localStorage.addTempAdvert(tempAdvert)
    }

    override fun update(tempAdvert: TempAdvert) {
        DBSessionFactoryUtil.localStorage.updateTempAdvert(tempAdvert)
    }

    override fun delete(userId: Long) {
        DBSessionFactoryUtil.localStorage.deleteTempAdvertByUserId(userId)
    }
}
