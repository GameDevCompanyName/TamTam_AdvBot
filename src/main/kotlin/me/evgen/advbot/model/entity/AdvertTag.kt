package me.evgen.advbot.model.entity

import me.evgen.advbot.db.TableName
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = TableName.ADVERT_TAG)
class AdvertTag(
    @Id
    val id: Long,
    @Column(name = "advert_id")
    val advertId: Long,
    @Column(name = "tag_id")
    val tagId: Long
)
