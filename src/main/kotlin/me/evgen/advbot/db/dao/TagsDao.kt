package me.evgen.advbot.db.dao

abstract class TagsDao<T>: Dao<T>() {
    abstract fun findTag(id: Long): T?
    abstract fun findAllTags(): List<T>
}