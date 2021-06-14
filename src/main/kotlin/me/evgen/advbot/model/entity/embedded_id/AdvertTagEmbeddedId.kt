package me.evgen.advbot.model.entity.embedded_id

import java.io.Serializable
import javax.persistence.Column

class AdvertTagEmbeddedId(
    @Column(name = "advert_id")
    val advertId: Long,
    @Column(name = "tag_id")
    val tagId: Long
) : Serializable
