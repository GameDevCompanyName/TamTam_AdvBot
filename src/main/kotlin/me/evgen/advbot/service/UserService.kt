package me.evgen.advbot.service

import me.evgen.advbot.db.dao.UserDaoImpl
import me.evgen.advbot.model.entity.User
import me.evgen.advbot.model.state.BaseState

object UserService {
    private val userDao = UserDaoImpl()

    fun findUser(id: Long): User? {
        return userDao.findUser(id)
    }

    fun insertUser(user: User) {
        userDao.insert(user)
    }

    fun updateUser(user: User) {
        userDao.update(user)
    }

    fun getCurrentState(id: Long): BaseState? {
        return userDao.findUser(id)?.getState()
    }
}
