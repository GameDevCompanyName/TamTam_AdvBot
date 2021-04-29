package db.entity

data class UserGroupEntity(
    var id: Long,
    var advertisements: Set<AdvertisementEntity>
)