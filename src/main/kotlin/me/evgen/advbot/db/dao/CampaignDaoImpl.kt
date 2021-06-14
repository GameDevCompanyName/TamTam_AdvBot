package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.Campaign
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
        return findAllByColumnName<Campaign>("ad_id", adId).toSet()
    }

    override fun deleteCampaign(postId: String) {
        //TODO
    }

}