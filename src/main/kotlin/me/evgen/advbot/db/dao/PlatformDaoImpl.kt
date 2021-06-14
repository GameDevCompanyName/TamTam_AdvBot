package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.AdvertTag
import me.evgen.advbot.model.entity.IPlatform
import me.evgen.advbot.model.entity.Platform
import me.evgen.advbot.model.entity.PlatformTag
import me.evgen.advbot.model.entity.embedded_id.AdvertTagEmbeddedId
import me.evgen.advbot.model.entity.embedded_id.PlatformTagEmbeddedId
import javax.persistence.criteria.Order
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class PlatformDaoImpl : PlatformDao<IPlatform>() {
    override fun findPlatform(id: Long): IPlatform? {
        return find<Platform>(id)
    }

    override fun findUserPlatforms(userId: Long): Set<IPlatform> {
        return findAllByColumnName<Platform>("user", userId).toSet()
    }

    override fun deletePlatform(id: Long) {
        delete<Platform>(id)
    }

    override fun findAllPlatforms(): List<IPlatform> {
        return findAll<Platform>().toList()
    }

    override fun findPlatformForAdvert(
        advertId: Long,
        isForward: Boolean,
        anchorPlatformId: Long,
        quantity: Int): List<IPlatform> {

        var result: List<Platform> = listOf()

        execute {
            val cb = it.criteriaBuilder

            val query = cb.createQuery(Platform::class.java)
            val platform: Root<Platform> = query.from(Platform::class.java)

            val queryPlatform = query.subquery(Long::class.java)
            val platformTag: Root<PlatformTag> = queryPlatform.from(PlatformTag::class.java)

            val queryTags = queryPlatform.subquery(Long::class.java)
            val advertTag: Root<AdvertTag> = queryTags.from(AdvertTag::class.java)

            queryTags
                .select(advertTag.get<AdvertTagEmbeddedId>("id").get("tagId"))
                .where(cb.equal(advertTag.get<AdvertTagEmbeddedId>("id").get<Long>("advertId"), advertId))
            queryPlatform
                .select(platformTag.get<PlatformTagEmbeddedId>("id").get("platformId"))
                .where(platformTag.get<PlatformTagEmbeddedId>("id").get<Long>("tagId").`in`(queryTags))
            query.select(platform)
            val idPredicate = platform.get<Long>("id").`in`(queryPlatform)
            val availabilityPredicate = cb.equal(platform.get<Boolean>("availability"), true)
            val platformOrder: Order
            val anchorPlatformIdPredicate: Predicate
            if (isForward) {
                platformOrder = cb.asc(platform.get<Long>("id"))
                anchorPlatformIdPredicate = cb.gt(platform.get<Long>("id"), anchorPlatformId)
            } else {
                platformOrder = cb.desc(platform.get<Long>("id"))
                anchorPlatformIdPredicate = cb.lt(platform.get<Long>("id"), anchorPlatformId)
            }
            result = if (anchorPlatformId == -1L) {
                query.where(cb.and(idPredicate, availabilityPredicate))
                query.orderBy(cb.asc(platform.get<Long>("id")))

                it.createQuery(query).setMaxResults(quantity).resultList
            } else {
                query.where(cb.and(idPredicate, availabilityPredicate, anchorPlatformIdPredicate))
                query.orderBy(platformOrder)

                it.createQuery(query).setMaxResults(quantity).resultList.reversed()
            }
        }

        return result
    }
}
