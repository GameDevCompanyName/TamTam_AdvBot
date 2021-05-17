package me.evgen.advbot

import chat.tamtam.botsdk.model.prepared.User
import me.evgen.advbot.model.TempAdvert
import me.evgen.advbot.model.state.BaseState
import java.lang.Exception

object BotController {
    private val statesMap = mutableMapOf<User, BaseState>()
    // на этапе создания реклама лежит в этой мапе, после нажатия на "готово" улетает в adsMap
    val tempAdMap = mutableMapOf<User, TempAdvert>()

    suspend fun moveTo(newState: BaseState, user: User, isForce: Boolean = false, onSuccess: suspend (BaseState) -> Unit) {
        var oldState = getCurrentState(user)
        if (isForce) {
            statesMap[user] = newState
            if (oldState == null) {
                oldState = newState
            }
        } else if (oldState != null && oldState.timestamp == newState.timestamp) {
            newState.timestamp = System.currentTimeMillis()
            statesMap[user] = newState
        } else {
            return
        }

        try {
            onSuccess.invoke(oldState)
        } catch (e: Exception) {
            println(e.localizedMessage)
            //TODO norm log
        }
    }

    fun getCurrentState(user: User): BaseState? = statesMap[user]
}
