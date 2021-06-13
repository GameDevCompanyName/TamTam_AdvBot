package me.evgen.advbot

import me.evgen.advbot.model.state.BaseState
import me.evgen.advbot.service.UserService
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.lang.Exception

object BotController {

    val paymentSupport = PropertyChangeSupport(this::class.java.simpleName)

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

            if (newState is PropertyChangeListener) {
                paymentSupport.addPropertyChangeListener(newState)
            }
            if (oldState is PropertyChangeListener) {
                paymentSupport.removePropertyChangeListener(oldState)
            }

            onSuccess.invoke(oldState)
        } catch (e: Exception) {
            e.printStackTrace()
            //TODO norm log
        }
    }
}
