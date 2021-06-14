package me.evgen.advbot.model.entity.embedded_id

import java.io.Serializable
import javax.persistence.Column

class PlatformTagEmbeddedId (
    @Column(name = "platform_id")
    var platformId: Long,
    @Column(name = "tag_id")
    var tagId: Long
) : Serializable
