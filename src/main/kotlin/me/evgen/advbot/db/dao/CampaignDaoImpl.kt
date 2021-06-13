package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.ICampaign

class CampaignDaoImpl : CampaignDao<ICampaign>() {
    override fun findCampaign(postId: String): ICampaign? {
        //TODO
        return null
    }

    override fun findCampaignByAdId(adId: Long): ICampaign? {
        //TODO
        return null
    }

    override fun findAllAdPosts(adId: Long): Set<ICampaign> {
        //TODO
        return emptySet()
    }

    override fun deleteCampaign(postId: String) {
        //TODO
    }

}