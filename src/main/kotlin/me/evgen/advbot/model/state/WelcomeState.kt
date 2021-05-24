package me.evgen.advbot.model.state

import chat.tamtam.botsdk.client.RequestsManager
import chat.tamtam.botsdk.state.StartedBotState
import me.evgen.advbot.getUser
import me.evgen.advbot.getUserId
import me.evgen.advbot.initialText
import me.evgen.advbot.model.entity.User
import me.evgen.advbot.service.UserService

class WelcomeState(timestamp: Long) : BaseState(timestamp), CustomStartedBotState {
    override suspend fun handle(startedBotState: StartedBotState, requestsManager: RequestsManager) {
        val user = UserService.findUser(startedBotState.getUserId().id)
        if (user == null) {
            val newUser = User(
                startedBotState.getUserId().id,
                this
            )
            UserService.insertUser(newUser)

            initialText(startedBotState.getUser().name).sendTo(startedBotState.getUserId(), requestsManager)
        } else {
            """С возвращением, ${startedBotState.getUser().name}!
                |Для начала работы введите команду /start
            """.trimMargin().sendTo(startedBotState.getUserId(), requestsManager)
            //TODO call handle current state?
        }
    }
}
