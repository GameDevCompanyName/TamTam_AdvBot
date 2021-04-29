package me.evgen.advbot.db.entity

data class AdvertisementEntity(
    var id: Long?,
    var name: String,
    var description: String?,
    var link: String?,
    var fileData: ByteArray?,
    var createdUserEntity: UserEntity?,
    var enabled: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdvertisementEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (createdUserEntity != other.createdUserEntity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + createdUserEntity.hashCode()
        return result
    }
}
