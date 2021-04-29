package me.evgen.advbot.callbacks

import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.scopes.CallbacksScope
import chat.tamtam.botsdk.state.CallbackState

suspend fun CallbacksScope.callbackPlatform(callbackState: CallbackState) {
    //                // send message with upload Photo which replace old message
//                "Предоставление площадки" prepareReplacementCurrentMessage
//                        AnswerParams(it.callback.callbackId, it.callback.user.userId) answerWith
//                        UploadParams("res/busy_dog.jpg", UploadType.PHOTO)

    // send message which replace old message
    ("section is under development") answerFor (callbackState.callback.callbackId)

    // send notification (as Toast) for User
    "Work in progress" answerNotification AnswerParams(
        callbackState.callback.callbackId,
        callbackState.callback.user.userId
    )
}
