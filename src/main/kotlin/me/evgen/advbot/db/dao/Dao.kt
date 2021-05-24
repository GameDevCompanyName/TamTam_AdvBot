package me.evgen.advbot.db.dao

import me.evgen.advbot.db.DBSessionFactoryUtil
import me.evgen.advbot.db.local.LocalStorage
import org.hibernate.SessionFactory

interface Dao {
    fun getDBSession(): SessionFactory? {
        return DBSessionFactoryUtil.sessionFactory
    }

    fun getLocalStorage(): LocalStorage {
        return DBSessionFactoryUtil.localStorage
    }
}
