package me.evgen.advbot.callbacks

import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.AnswerParams
import chat.tamtam.botsdk.scopes.CallbacksScope
import chat.tamtam.botsdk.state.CallbackState
import me.evgen.advbot.*
import me.evgen.advbot.storage.LocalStorage

suspend fun CallbacksScope.callbackAdvert(callbackState: CallbackState) {
    BotController.statesMap[callbackState.callback.user] = States.NORMAL

    val inlineKeyboard = createAdvertKeyboard()
    "Размещение рекламы" prepareReplacementCurrentMessage
            AnswerParams(callbackState.callback.callbackId, callbackState.callback.user.userId) answerWith inlineKeyboard
}

suspend fun CallbacksScope.callbackAdvTitle(callbackState: CallbackState) {
    BotController.statesMap[callbackState.callback.user] = States.AD_NAMING

    """Введите название будущей рекламы.
       |Название используется для идентификации и в самом объявлении показано не будет.
    """.trimMargin() prepareReplacementCurrentMessage
            AnswerParams(
                callbackState.callback.callbackId,
                callbackState.callback.user.userId
            ) answerWith constructorCancelKeyboard()
}

suspend fun CallbacksScope.callbackAdvText(callbackState: CallbackState) {
    BotController.statesMap[callbackState.callback.user] = States.AD_TEXTING

    """Введите текст будущей рекламы.
       |Именно этот текст будет показан в объявлении.
    """.trimMargin() prepareReplacementCurrentMessage
            AnswerParams(
                callbackState.callback.callbackId,
                callbackState.callback.user.userId
            ) answerWith constructorCancelKeyboard()
}

suspend fun CallbacksScope.callbackAdvList(callbackState: CallbackState) {
    val ads = LocalStorage.getAds(callbackState.callback.user)

    val inlineKeyboard = keyboard {
        for (entry in ads) {
            +buttonRow {
                +Button(
                    ButtonType.CALLBACK,
                    entry.title,
                    payload = Payloads.getAdvSettingsPayload(entry.id)
                )
            }
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "⬅ Назад",
                payload = Payloads.ADVERT
            )
        }
    }

    for (entry in ads) {
        answerOnCallback(Payloads.getAdvSettingsPayload(entry.id)) { callbackOnAd ->
            val argsPayload = Payloads.parsePayload(callbackOnAd.callback.payload)
            val advId = argsPayload[Payloads.KEY_ADV_ID]?.toLong()
            if (advId != null) {
                val advert = LocalStorage.getAd(
                    callbackOnAd.callback.user,
                    advId
                )
                """Работа с рекламой: ${advert?.title ?: "UNKNOWN"}""" prepareReplacementCurrentMessage
                        AnswerParams(
                            callbackOnAd.callback.callbackId,
                            callbackOnAd.callback.user.userId
                        ) answerWith createAdvSettingsKeyboard()
            } else {
                "Ошибка! Нет такой рекламы." answerNotification
                        AnswerParams(callbackOnAd.callback.callbackId, callbackOnAd.callback.user.userId)
            }
        }
    }

    if (ads.isNotEmpty()) "Ваши объявления:" prepareReplacementCurrentMessage
            AnswerParams(callbackState.callback.callbackId, callbackState.callback.user.userId) answerWith inlineKeyboard
    else "Здесь будут отображаться ваши объявления" prepareReplacementCurrentMessage
            AnswerParams(callbackState.callback.callbackId, callbackState.callback.user.userId) answerWith inlineKeyboard
}
