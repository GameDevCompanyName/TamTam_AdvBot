package me.evgen.advbot

import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.service.UserService
import java.lang.Exception

object BotController {
    suspend fun moveTo(newState: BaseState, userId: Long, isForce: Boolean = false, onSuccess: suspend (BaseState) -> Unit) {
        val user = UserService.findUser(userId) ?: return
        var oldState = if (!isForce) user.getState() else null
        when {
            isForce -> {
                oldState = newState
            }
            oldState?.timestamp == newState.timestamp -> {
                newState.timestamp = System.currentTimeMillis()
            }
            else -> {
                return
            }
        }

        try {
            user.payload = newState.toPayload().toJson()
            UserService.updateUser(user)

            onSuccess.invoke(oldState)
        } catch (e: Exception) {
            e.printStackTrace()
            //TODO norm log
        }
    }
}
