package me.evgen.advbot.db.service

import com.vladsch.kotlin.jdbc.HikariCP
import com.vladsch.kotlin.jdbc.SessionImpl

abstract class AbstractDbService<T> {

    init {
        HikariCP.default("jdbc:postgresql://localhost:5432/adv_bot_db", "postgres", "postgres")
        SessionImpl.defaultDataSource = { HikariCP.dataSource() }
    }

    abstract fun getById(id: Long): T?

    abstract fun saveEntity(entity: T)

    abstract fun deleteEntity(entity: T)

    abstract fun findAll(): List<T>
}