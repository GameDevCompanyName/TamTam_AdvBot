package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.User

class UserDaoImpl : UserDao {
    override fun findById(id: Long): User? {
        return try {
            var user: User? = null
            getDBSession()?.openSession()?.apply {
                beginTransaction().apply {
                    user = get(User::class.java, id)
                    commit()
                }
                close()
            }
            user
        } catch (ex: Exception) {
            ex.printStackTrace()
            //TODO log error
            null
        }
    }

    override fun insert(user: User) {
        try {
            getDBSession()?.openSession()?.apply {
                beginTransaction().apply {
                    save(user)
                    commit()
                }
                close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            //TODO log error
        }
    }

    override fun update(user: User) {
        try {
            getDBSession()?.openSession()?.apply {
                beginTransaction().apply {
                    update(user)
                    commit()
                }
                close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            //TODO log error
        }
    }

    override fun delete(id: Long) {
        try {
            getDBSession()?.openSession()?.apply {
                beginTransaction().apply {
                    createQuery("delete ${User::class.java.name} where id = :id").setParameter("id", id).executeUpdate()
                    commit()
                }
                close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            //TODO log error
        }
    }
}
