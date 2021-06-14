package me.evgen.advbot.model

enum class ErrorType(val errorMessage: String) {
    DEFAULT("Ошибка!"),
    CREATE_ADVERT("Не удалось создать рекламу."),
    EDIT_ADVERT("Не удалось получить информацию о рекламе."),
    POST_ADVERT("Не удалось опубликовать рекламу."),
}
