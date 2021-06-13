package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.User

class UserDaoImpl : UserDao<User>() {
    override fun findUser(id: Long): User? {
        return find(id)
    }

    override fun deleteUser(id: Long) {
        delete<User>(id)
    }
}
