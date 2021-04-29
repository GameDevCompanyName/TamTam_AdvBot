package me.evgen.advbot.db.service

import com.vladsch.kotlin.jdbc.Row
import com.vladsch.kotlin.jdbc.sqlQuery
import com.vladsch.kotlin.jdbc.usingDefault
import me.evgen.advbot.db.entity.AdvertisementEntity

class AdvertisementDbService: AbstractDbService<AdvertisementEntity>() {

    private val userDbService: UserDbService = UserDbService()

    private val toAdvertisementEntity: (Row) -> AdvertisementEntity = { row ->
        AdvertisementEntity(
            row.long("id"),
            row.string("name"),
            row.string("description"),
            row.string("link"),
            row.bytes("file_data"),
            userDbService.getById(row.long("created_user_id")),
            row.boolean("enabled")
        )
    }

    override fun getById(id: Long): AdvertisementEntity? {
        return usingDefault { session -> session.first(sqlQuery("select * from advertisement where id = ?", id), toAdvertisementEntity) }
    }

    override fun saveEntity(entity: AdvertisementEntity) {
        if (entity.id != null) {
            usingDefault { session -> session.update(sqlQuery("update advertisement " +
                    "set name = ?,description = ?, link = ?, file_data = ?, enabled = ?)" +
                    " where id = ?", entity.name, entity.description, entity.link, entity.fileData, entity.enabled, entity.id)) }
        } else {
            usingDefault { session -> session.update(sqlQuery("insert into advertisement (name, description, link, file_data, created_user_id, enabled)" +
                    " values (?, ?, ?, ?, ?, ?)", entity.name, entity.description, entity.link, entity.fileData, entity.createdUserEntity?.id, entity.enabled)) }
        }
    }

    override fun deleteEntity(entity: AdvertisementEntity) {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<AdvertisementEntity> {
        return usingDefault { session -> session.list(sqlQuery("select * from advertisement"), toAdvertisementEntity) }
    }

    fun findAllByCreatedUserId(id: Long): List<AdvertisementEntity> {
        return usingDefault { session -> session.list(sqlQuery("select * from advertisement where created_user_id = ?", id), toAdvertisementEntity) }
    }
}