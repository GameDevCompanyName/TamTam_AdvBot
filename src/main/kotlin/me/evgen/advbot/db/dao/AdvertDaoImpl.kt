package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.Advert
import java.lang.Exception

class AdvertDaoImpl : AdvertDao {
    override fun findById(id: Long): Advert? {
        var advert: Advert? = null
        try {
            getDBSession()?.openSession()?.apply {
                beginTransaction().apply {
                    advert = get(Advert::class.java, id)
                    commit()
                }
                close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            //TODO log error
        } finally {
            return advert
        }
    }

    override fun insert(advert: Advert) {
        try {
            getDBSession()?.openSession()?.apply {
                beginTransaction().apply {
                    save(advert)
                    commit()
                }
                close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            //TODO log error
        }
    }

    override fun update(advert: Advert) {
        try {
            getDBSession()?.openSession()?.apply {
                beginTransaction().apply {
                    update(advert)
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
                    createQuery("delete ${Advert::class.java.name} where id = :id").setParameter("id", id).executeUpdate()
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
