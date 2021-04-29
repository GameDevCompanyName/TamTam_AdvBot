package me.evgen.advbot.db.entity

data class UserGroupEntity(
    var id: Long,
    var advertisements: Set<AdvertisementEntity>
)