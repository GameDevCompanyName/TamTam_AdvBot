package model

import chat.tamtam.botsdk.keyboard.keyboard
import chat.tamtam.botsdk.model.Button
import chat.tamtam.botsdk.model.ButtonType
import chat.tamtam.botsdk.model.request.InlineKeyboard
import Payloads

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
                payload = Payloads.ADVERT
            )
            +Button(
                ButtonType.CALLBACK,
                "Предоставить площадку",
                payload = Payloads.PLATFORM
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
                payload = Payloads.ADV_LIST
            )
            +Button(
                ButtonType.CALLBACK,
                "Создать рекламу",
                payload = Payloads.CONSTRUCT
            )
        }

        this add buttonRow {
            this add Button(
                ButtonType.CALLBACK,
                "⬅ Назад",
                payload = Payloads.BACK_TO_START
            )
        }
    }
}

fun createAdvSettingsKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настроить рекламу ⚙",
                payload = Payloads.WIP
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Просмотр метрик 📊",
                payload = Payloads.WIP
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Отключить рекламу 🔒",
                payload = Payloads.WIP
            )
        }
        this add buttonRow {
            this add Button(
                ButtonType.CALLBACK,
                "⬅ Назад",
                payload = Payloads.ADV_LIST
            )
        }
    }
}

fun createConstructorKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка названия ✏",
                payload = Payloads.ADV_NAME
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка текста 📃",
                payload = Payloads.ADV_TEXT
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка изображения 📺",
                payload = Payloads.ADV_IMG
            )
        }
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "Настройка каналов 📢",
                payload = Payloads.ADV_TARGETS
            )
        }

        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "❌ Отмена",
                payload = Payloads.ADVERT
            )
            +Button(
                ButtonType.CALLBACK,
                "Готово ✅",
                payload = Payloads.ADVERT
            )
        }
    }
}

fun constructorCancelKeyboard(): InlineKeyboard {
    return keyboard {
        +buttonRow {
            +Button(
                ButtonType.CALLBACK,
                "⬅ Назад",
                payload = Payloads.ADVERT
            )
        }
    }
}