package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.Tag

abstract class PlatformDao<T> : Dao<T>() {
    abstract fun findPlatform(id: Long): T?
    abstract fun findUserPlatforms(userId: Long): Set<T>
    abstract fun deletePlatform(id: Long)
    abstract fun findAllPlatforms(): List<T>
    abstract fun findAllPlatformsByTags(tags: Set<Tag>): List<T>
    abstract fun findPlatformForAdvert(advertId: Long, isForward: Boolean, anchorId: Long, quantity: Int): List<T>
}