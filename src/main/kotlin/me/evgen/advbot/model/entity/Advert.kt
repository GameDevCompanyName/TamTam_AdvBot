package me.evgen.advbot.model.entity

import me.evgen.advbot.db.TableName
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = TableName.ADVERT)
class Advert() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1
    lateinit var title: String
    lateinit var text: String

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "${TableName.USER}_id")
    lateinit var owner: User

    constructor(title: String, owner: User) : this () {
        this.title = title
        this.text = ""
        this.owner = owner
    }
}
