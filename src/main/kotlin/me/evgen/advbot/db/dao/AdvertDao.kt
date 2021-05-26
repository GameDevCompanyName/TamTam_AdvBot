package me.evgen.advbot.db.dao

abstract class AdvertDao<T> : Dao<T>() {
    abstract fun findAdvert(id: Long): T?
    abstract fun findAdverts(userId: Long): List<T>
    abstract fun deleteAdvert(id: Long)
}
