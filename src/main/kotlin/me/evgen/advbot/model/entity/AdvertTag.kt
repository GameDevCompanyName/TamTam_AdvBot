package me.evgen.advbot.model.entity

import me.evgen.advbot.db.TableName
import me.evgen.advbot.model.entity.embedded_id.AdvertTagEmbeddedId
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.EmbeddedId

@Entity
@Table(name = TableName.ADVERT_TAG)
class AdvertTag(
    @EmbeddedId
    val id: AdvertTagEmbeddedId
)
