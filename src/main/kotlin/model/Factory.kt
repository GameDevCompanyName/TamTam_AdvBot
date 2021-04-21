package model

import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard

fun initialText(name: String): String {
    return """Привет, $name! Я бот для работы с рекламой.
                |Для начала работы введите команду /start
            """.trimMargin()
}

fun createStartKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Разместить рекламу",
                payload = "ADVERT"
            )
            +Button(
                ButtonType.CALLBACK,
                "Предоставить площадку",
                payload = "PLATFORM"
            )
        }
    }
}

fun createAdvertKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Мои объявления",
                payload = "ADV_LIST"
            )
            +Button(
                ButtonType.CALLBACK,
                "Создать рекламу",
                payload = "CONSTRUCT"
            )
        }

        this add buttonRow {
            this add Button(
                ButtonType.CALLBACK,
                "← Назад",
                payload = "BACK_TO_START"
            )
        }
    }
}

fun createConstructorKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка названия",
                payload = "ADV_NAME"
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка текста",
                payload = "ADV_TEXT"
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка изображения",
                payload = "ADV_IMG"
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка каналов",
                payload = "ADV_TARGETS"
            )
        }

        this add buttonRow {
            this add Button(
                ButtonType.CALLBACK,
                "← Назад",
                payload = "BACK_TO_ADVERT"
            )
        }
    }
}