package me.evgen.advbot

fun initialText(name: String): String {
    return """Привет, $name! Я бот для работы с рекламой.
                |Для начала работы введите команду /start
            """.trimMargin()
}

