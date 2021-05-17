package me.evgen.advbot.model

class TempAdvert() {
    var title: String = ""
    var text: String = ""

    constructor(advert: Advert) : this() {
        title = advert.title
        text = advert.text
    }
}
