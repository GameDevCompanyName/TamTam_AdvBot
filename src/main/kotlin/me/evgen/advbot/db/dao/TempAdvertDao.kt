package me.evgen.advbot.db.dao

abstract class TempAdvertDao<T> : Dao<T>() {
    abstract fun findByUserId(userId: Long): T?
    abstract fun deleteTempAdvert(userId: Long)
}
