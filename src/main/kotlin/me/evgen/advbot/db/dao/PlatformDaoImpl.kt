package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.IPlatform
import me.evgen.advbot.model.entity.Platform

class PlatformDaoImpl : PlatformDao<IPlatform>() {
    override fun findPlatform(id: Long): IPlatform? {
        return find<Platform>(id)
    }

    override fun findUserPlatforms(userId: Long): Set<IPlatform> {
        return findAllByColumnName<Platform>("user", userId).toSet()
    }

    override fun deletePlatform(id: Long) {
        delete<Platform>(id)
    }
}
