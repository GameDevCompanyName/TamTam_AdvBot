package me.evgen.advbot.model.entity

import me.evgen.advbot.db.TableName
import me.evgen.advbot.model.entity.embedded_id.PlatformTagEmbeddedId
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.EmbeddedId

@Entity
@Table(name = TableName.PLATFORM_TAG)
class PlatformTag(
    @EmbeddedId
    val id: PlatformTagEmbeddedId
)
