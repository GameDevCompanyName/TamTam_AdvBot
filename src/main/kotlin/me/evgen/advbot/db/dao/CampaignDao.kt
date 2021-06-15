package me.evgen.advbot.db.dao

abstract class CampaignDao<T>: Dao<T>() {
    abstract fun findCampaign(postId: String): T?
    abstract fun findAllAdPosts(adId: Long): List<T>
    abstract fun deleteCampaign(postId: String)
}