package me.evgen.advbot.model.navigation

import com.google.gson.Gson
import me.evgen.advbot.model.state.BaseState
import kotlin.reflect.KClass

class Payload(clazz: KClass<out BaseState>, val jsonState: String) {
    val className: String = clazz.qualifiedName ?: "BaseState"

    fun toJson(): String {
        return Gson().toJson(this)
    }
}
