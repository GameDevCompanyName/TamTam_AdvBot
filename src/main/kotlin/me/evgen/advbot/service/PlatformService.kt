package me.evgen.advbot.service

import me.evgen.advbot.db.dao.PlatformDaoImpl
import me.evgen.advbot.db.dao.UserDaoImpl
import me.evgen.advbot.model.AdPlatform
import me.evgen.advbot.model.IPlatform
import me.evgen.advbot.model.entity.User
import me.evgen.advbot.model.state.WelcomeState

object PlatformService {
    private val platformDao = PlatformDaoImpl()
    private val userDao = UserDaoImpl()

    fun getPlatforms(userId: Long): Set<IPlatform> {
        return platformDao.findUserPlatforms(userId)
    }

    fun getPlatform(userId: Long, chatId: Long): IPlatform? {
        return platformDao.findPlatform(userId, chatId)
    }

    fun tagSwitchPlatform(userId: Long, platform: IPlatform, tag: String) {
        platformDao.findUserPlatforms(userId).first { it.id == platform.id }.apply {
            if (!tags.contains(tag)) {
                tags.add(tag)
            } else tags.remove(tag)
        }
    }

    fun addPlatform(userId: Long, chatId: Long) {
        var user = userDao.findById(userId)
        if (user == null) {
            userDao.insert(User(userId, WelcomeState(System.currentTimeMillis())))
        }
        user = userDao.findById(userId) //TODO check null after insert
        platformDao.insert(createAdPlatformFromChat(user!!, chatId))
    }

    private fun createAdPlatformFromChat(user: User, chatId: Long): AdPlatform {
        return AdPlatform(
            chatId,
            mutableSetOf(),
            false,
            user
        )
    }
}