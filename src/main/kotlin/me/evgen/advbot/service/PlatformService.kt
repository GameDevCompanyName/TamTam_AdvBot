package me.evgen.advbot.service

import me.evgen.advbot.db.dao.PlatformDaoImpl
import me.evgen.advbot.db.dao.UserDaoImpl
import me.evgen.advbot.model.entity.IPlatform
import me.evgen.advbot.model.entity.Platform
import me.evgen.advbot.model.entity.User
import me.evgen.advbot.model.state.WelcomeState

object PlatformService {
    private val platformDao = PlatformDaoImpl()
    private val userDao = UserDaoImpl()

    fun hasMorePlatformsForPosting(
        anchorId: Long,
        quantity: Int,
        isForward: Boolean,
        advertId: Long
    ): Boolean {
        return getPlatformsForPosting(anchorId, quantity, isForward, advertId).isNotEmpty()
    }

    fun getPlatformsForPosting(
        anchorId: Long,
        quantity: Int,
        isForward: Boolean,
        advertId: Long): List<IPlatform> {

        return platformDao.findPlatformForAdvert(advertId, isForward, anchorId, quantity)
    }

    fun getPlatforms(userId: Long): Set<IPlatform> {
        return platformDao.findUserPlatforms(userId)
    }

    fun getPlatform(chatId: Long): IPlatform? {
        return platformDao.findPlatform(chatId)
    }

    fun tagSwitchPlatform(platform: IPlatform, tag: String) {
        platform.apply {
            if (!tags.contains(tag)) {
                tags.add(tag)
            } else tags.remove(tag)
        }

        platformDao.update(platform)
    }

    fun getAllPlatforms(): List<IPlatform> {
        return platformDao.findAllPlatforms()
    }

    fun accessSwitch(platform: IPlatform) {
        platform.availability = !platform.availability
        platformDao.update(platform)
    }

    fun addPlatform(userId: Long, chatId: Long) {
        var user = userDao.findUser(userId)
        if (user == null) {
            userDao.insert(User(userId, WelcomeState(System.currentTimeMillis())))
        }
        user = userDao.findUser(userId) //TODO check null after insert
        platformDao.insert(createAdPlatformFromChat(user!!, chatId))
    }

    fun deletePlatform(id: Long) {
        platformDao.deletePlatform(id)
    }

    private fun createAdPlatformFromChat(user: User, chatId: Long): IPlatform {
        return Platform(
            chatId,
            false,
            user
        )
    }
}
