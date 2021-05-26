package me.evgen.advbot.db.dao

abstract class UserDao<T> : Dao<T>() {
    abstract fun findUser(id: Long): T?
    abstract fun deleteUser(id: Long)
}
