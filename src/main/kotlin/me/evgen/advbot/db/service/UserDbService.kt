package me.evgen.advbot.db.service

import com.vladsch.kotlin.jdbc.Row
import com.vladsch.kotlin.jdbc.sqlQuery
import com.vladsch.kotlin.jdbc.usingDefault
import me.evgen.advbot.db.entity.UserEntity

class UserDbService: AbstractDbService<UserEntity>() {

    private val toUser: (Row) -> UserEntity = { row ->
        UserEntity(
            row.long("id")
        )
    }

    override fun getById(id: Long): UserEntity? {
        return usingDefault { session -> session.first(sqlQuery("select * from users where id = ?", id), toUser) }
    }

    override fun saveEntity(entity: UserEntity) {
        usingDefault { session -> session.update(sqlQuery("insert into users (id) values (?)", entity.id)) }
    }

    override fun deleteEntity(entity: UserEntity) {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<UserEntity> {
        return usingDefault { session -> session.list(sqlQuery("select * from users"), toUser) }
    }

}

