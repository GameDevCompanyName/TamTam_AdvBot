package me.evgen.advbot.model.entity

import me.evgen.advbot.db.TableName
import javax.persistence.*

@Entity
@Table(name = TableName.TAG)
class Tag {
    @Id
    var id: Long = -1

    lateinit var name: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tag

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}