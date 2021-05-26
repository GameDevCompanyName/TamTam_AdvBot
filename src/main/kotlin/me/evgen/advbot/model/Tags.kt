package me.evgen.advbot.model

//TODO move to DB
object Tags {
    const val SPORTS = "СПОРТ"
    const val NEWS = "НОВОСТИ"
    const val GAMES = "ИГРЫ"
    const val POLITICS = "ПОЛИТИКА"
    const val TECH = "ТЕХНОЛОГИИ"
    const val ECONOMICS = "ЭКОНОМИКА"

    fun getAllTags(): Set<String> {
        val res = mutableSetOf<String>()
        res.add(SPORTS)
        res.add(NEWS)
        res.add(GAMES)
        res.add(POLITICS)
        res.add(TECH)
        res.add(ECONOMICS)
        return res
    }
}