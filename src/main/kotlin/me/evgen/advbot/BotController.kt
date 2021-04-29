package me.evgen.advbot

import chat.tamtam.botsdk.model.prepared.User
import me.evgen.advbot.model.TempAdvert

object BotController {
    val statesMap = mutableMapOf<User, States>()
    // на этапе создания реклама лежит в этой мапе, после нажатия на "готово" улетает в adsMap
    val tempAdMap = mutableMapOf<User, TempAdvert>()
}
