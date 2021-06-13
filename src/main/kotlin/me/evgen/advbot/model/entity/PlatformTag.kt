package me.evgen.advbot.model.entity

import me.evgen.advbot.db.TableName
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = TableName.PLATFORM_TAG)
class PlatformTag(
    @Id
    val id: Long,
    @Column(name = "platform_id")
    var platformId: Long,
    @Column(name = "tag_id")
    var tagId: Long
)
