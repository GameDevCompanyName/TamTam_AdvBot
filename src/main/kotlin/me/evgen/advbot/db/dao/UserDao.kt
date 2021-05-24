package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.User

interface UserDao : Dao {
    fun findById(id: Long): User?
    fun insert(user: User)
    fun update(user: User)
    fun delete(id: Long)
}
