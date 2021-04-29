package me.evgen.advbot

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

    fun getAdvSettingsPayload(advId: Long): String {
        return "$ADV_SETTINGS/$advId"
    }
}