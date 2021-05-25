package me.evgen.advbot.db.dao

import chat.tamtam.botsdk.model.UserId
import me.evgen.advbot.db.DBSessionFactoryUtil
import me.evgen.advbot.model.IPlatform

class PlatformDaoImpl : PlatformDao {
    override fun findPlatform(userId: Long, chatId: Long): IPlatform? {
        return DBSessionFactoryUtil.localStorage.getPlatform(userId, chatId)
    }

    override fun findUserPlatforms(userId: Long): Set<IPlatform> {
        return DBSessionFactoryUtil.localStorage.getPlatforms(userId)
    }

    override fun update(platform: IPlatform) {
        //TODO
    }

    override fun insert(platform: IPlatform) {
        DBSessionFactoryUtil.localStorage.addChat(platform)
    }

}