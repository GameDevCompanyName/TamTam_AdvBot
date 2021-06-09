package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.IPlatform
import me.evgen.advbot.model.entity.Platform
import me.evgen.advbot.model.entity.Tag
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

    override fun findPlatformForAdvert(advertId: Long, isForward: Boolean, anchorId: Long, quantity: Int): List<IPlatform> {
        var result: List<Platform> = listOf()

        if (anchorId == -1L) {
            execute {
                val cb = it.criteriaBuilder
                val query = cb.createQuery(Platform::class.java)
                val platform: Root<Platform> = query.from(Platform::class.java)

                val availabilityPredicate = cb.equal(platform.get<Boolean>("availability"), true)

                query.select(platform)
                query.where(availabilityPredicate)
                query.orderBy(cb.asc(platform.get<Long>("id")))

                result = it.createQuery(query).setMaxResults(quantity).resultList
            }
        } else {
            execute {
                val cb = it.criteriaBuilder
                val query = cb.createQuery(Platform::class.java)
                val platform: Root<Platform> = query.from(Platform::class.java)

                val availabilityPredicate = cb.equal(platform.get<Boolean>("availability"), true)
                val idPredicate: Predicate

                query.select(platform)
                if (isForward) {
                    idPredicate = cb.gt(platform.get<Long>("id"), anchorId)
                    query.where(cb.and(idPredicate, availabilityPredicate))
                    query.orderBy(cb.asc(platform.get<Long>("id")))
                    result = it.createQuery(query).setMaxResults(quantity).resultList
                } else {
                    idPredicate = cb.lt(platform.get<Long>("id"), anchorId)
                    query.where(cb.and(idPredicate, availabilityPredicate))
                    query.orderBy(cb.desc(platform.get<Long>("id")))
                    result = it.createQuery(query).setMaxResults(quantity).resultList.reversed()
                }
            }
        }

        return result
    }

    override fun findAllPlatformsByTags(tags: Set<Tag>): List<IPlatform> {
        return findAllPlatforms().filter{platform -> platform.tags.any{tag -> tags.contains(tag)}}
    }
}
