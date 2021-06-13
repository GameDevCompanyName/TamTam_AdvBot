package me.evgen.advbot.model.payment

import chat.tamtam.botsdk.client.RequestsManager

data class PaymentStatusEvent(
    val status: Boolean,
    val userId: Long,
    val requestsManager: RequestsManager) {

    companion object {
        const val PROPERTY_NAME = "pay_status"
    }

}
