package me.evgen.advbot.model.entity

import me.evgen.advbot.db.TableName
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = TableName.ADVERT)
class Advert() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1
    var title: String = ""
    var text: String = ""

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "${TableName.USER}_id")
    lateinit var owner: User

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = TableName.ADVERT_TAG,
        joinColumns = [JoinColumn(name = "${TableName.ADVERT}_id")],
        inverseJoinColumns = [JoinColumn(name = "${TableName.TAG}_id")]
    )
    lateinit var tags: MutableSet<Tag>

    constructor(title: String, owner: User) : this () {
        this.title = title
        this.text = ""
        this.owner = owner
    }
}
