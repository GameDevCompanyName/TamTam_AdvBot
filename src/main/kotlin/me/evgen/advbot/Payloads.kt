package me.evgen.advbot

import com.google.common.base.Splitter

object Payloads {
    const val PLATFORM = "PLATFORM"
    const val BACK_TO_START = "BACK_TO_START"

    const val ADVERT = "ADVERT"
    const val ADV_SETTINGS = "ADV_SETTINGS"
    const val ADV_LIST = "ADV_LIST"
    const val ADV_TITLE = "ADV_NAME"
    const val ADV_TEXT = "ADV_TEXT"
    const val ADV_IMG = "ADV_IMG"
    const val ADV_TARGETS = "ADV_TARGETS"

    const val MAKER_DONE = "MAKER_DONE"

    const val WIP = "WIP"
    const val TEST = "TEST"

    const val KEY_PAYLOAD = "PAYLOAD"
    const val KEY_ADV_ID = "ADV_ID"

    fun getAdvSettingsPayload(advId: Long): String {
        return "$ADV_SETTINGS?$KEY_ADV_ID=$advId"
    }

    fun parsePayload(payload: String): Map<String, String> {
        val args = payload.split("?")
        return if (args.size == 1) {
            mutableMapOf<String, String>().apply {
                put(KEY_PAYLOAD, args[0])
            }
        } else {
            Splitter.on("&").withKeyValueSeparator("=").split(args[1]).toMutableMap().apply {
                put(KEY_PAYLOAD, args[0])
            }
        }
    }
}