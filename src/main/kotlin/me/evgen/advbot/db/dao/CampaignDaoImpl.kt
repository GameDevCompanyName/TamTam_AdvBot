package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.Campaign
import me.evgen.advbot.model.entity.ICampaign

class CampaignDaoImpl : CampaignDao<ICampaign>() {
    override fun findCampaign(postId: String): ICampaign? {
        return find<Campaign>(postId)
    }

    override fun findAllAdPosts(adId: Long): List<ICampaign> {
        return findAllByColumnName<Campaign>("advert", adId).toList()
    }

    override fun deleteCampaign(postId: String) {
        //TODO
    }
}
