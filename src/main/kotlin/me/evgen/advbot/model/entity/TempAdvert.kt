package me.evgen.advbot.model.entity

class TempAdvert() {

    var id: Long = -1
    lateinit var title: String
    lateinit var text: String
    lateinit var owner: User

    constructor(id: Long, title: String, text: String, owner: User) : this() {
        this.id = id
        this.title = title
        this.text = text
        this.owner = owner
    }

    constructor(advert: Advert) : this() {
        this.id = advert.id
        this.title = advert.title
        this.text = advert.text
        this.owner = advert.owner
    }
}
