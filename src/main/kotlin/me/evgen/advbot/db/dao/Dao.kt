package me.evgen.advbot.db.dao

import me.evgen.advbot.db.DBSessionFactoryUtil
import me.evgen.advbot.db.local.LocalStorage
import org.hibernate.Session
import org.hibernate.SessionFactory
import javax.persistence.criteria.Root

abstract class Dao<T> {
    protected fun getDBSession(): SessionFactory? {
        return DBSessionFactoryUtil.sessionFactory
    }

    protected fun getLocalStorage(): LocalStorage {
        return DBSessionFactoryUtil.localStorage
    }

    protected fun execute(action: (Session) -> Unit) {
        try {
            DBSessionFactoryUtil.sessionFactory.openSession()?.apply {
                val transaction = beginTransaction()
                action.invoke(this)
                transaction.commit()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            //TODO log error
        }
    }

    open fun insert(entity: T) {
        execute {
            it.save(entity)
        }
    }

    open fun update(entity: T) {
        execute {
            it.update(entity)
        }
    }

    protected inline fun <reified T> delete(id: Long) {
        execute {
            val cb = it.criteriaBuilder
            val query = cb.createCriteriaDelete(T::class.java)
            val root: Root<T> = query.from(T::class.java)
            val request = query.where(cb.equal(root.get<T>("id"), id))

            it.createQuery(request).executeUpdate()
        }
    }

    protected inline fun <reified T> find(id: Long): T? {
        var result: T? = null
        execute {
            result = it.get(T::class.java, id)
        }

        return result
    }

    protected inline fun <reified T> findAll(): Collection<T> {
        var result: List<T> = listOf()
        execute {
            val cb = it.criteriaBuilder
            val query = cb.createQuery(T::class.java)
            val root: Root<T> = query.from(T::class.java)
            val request = query.select(root)

            result = it.createQuery(request).resultList
        }

        return result
    }

    protected inline fun <reified T> findAllByColumnName(
        columnName: String,
        value: Any): Collection<T> {

        var result: List<T> = listOf()
        execute {
            val cb = it.criteriaBuilder
            val query = cb.createQuery(T::class.java)
            val root: Root<T> = query.from(T::class.java)
            val request = query.select(root).where(cb.equal(root.get<T>(columnName), value))

            result = it.createQuery(request).resultList
        }

        return result
    }
}
