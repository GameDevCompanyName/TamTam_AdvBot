package me.evgen.advbot.model.navigation

import com.google.gson.Gson
import me.evgen.advbot.model.state.BaseState
import kotlin.reflect.KClass

class Payload(clazz: KClass<out BaseState>, val jsonState: String) {
    var className: String = clazz.qualifiedName ?: "BaseState"

    constructor(clazz: KClass<out BaseState>, state: BaseState) : this(clazz, state.toJson()) {
        this.className = clazz.qualifiedName ?: "BaseState"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}
