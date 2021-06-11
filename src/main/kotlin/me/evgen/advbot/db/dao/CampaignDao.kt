package me.evgen.advbot.db.dao

abstract class CampaignDao<T>: Dao<T>() {
    abstract fun findCampaign(postId: String): T?
    abstract fun findCampaignByAdId(adId: Long): T?
    abstract fun findAllAdPosts(adId: Long): Set<T>
    abstract fun deleteCampaign(postId: String)
}