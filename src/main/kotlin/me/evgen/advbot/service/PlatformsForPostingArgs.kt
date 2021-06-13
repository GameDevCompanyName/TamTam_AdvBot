package me.evgen.advbot.service

data class PlatformsForPostingArgs(
    val advertId: Long,
    val anchorPlatformId: Long = -1,
    val isForward: Boolean = true,
    val pageNum: Int = 1,
    val quantity: Int = QUANTITY_PLATFORM_ON_PAGE
) {
    companion object {
        private const val QUANTITY_PLATFORM_ON_PAGE = 2
    }

    fun next(anchorPlatformId: Long): PlatformsForPostingArgs {
        return PlatformsForPostingArgs(
            advertId,
            anchorPlatformId,
            isForward = true,
            pageNum = pageNum + 1,
            quantity = QUANTITY_PLATFORM_ON_PAGE
        )
    }

    fun previous(anchorPlatformId: Long): PlatformsForPostingArgs {
        return PlatformsForPostingArgs(
            advertId,
            anchorPlatformId,
            isForward = false,
            pageNum = pageNum - 1,
            quantity = QUANTITY_PLATFORM_ON_PAGE
        )
    }
}
