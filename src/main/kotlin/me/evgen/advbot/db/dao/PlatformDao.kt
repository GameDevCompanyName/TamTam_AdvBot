package me.evgen.advbot.db.dao

import me.evgen.advbot.model.IPlatform

interface PlatformDao : Dao {
    fun findPlatform(userId: Long, chatId: Long): IPlatform?
    fun findUserPlatforms(userId: Long): Set<IPlatform>
    fun update(platform: IPlatform)
    fun insert(platform: IPlatform)
}