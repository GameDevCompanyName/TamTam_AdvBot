package me.evgen.advbot.db.dao

import me.evgen.advbot.model.entity.Tag

class TagsDaoImpl: TagsDao<Tag>() {
    override fun findTag(id: Long): Tag? {
        return find<Tag>(id)
    }

    override fun findAllTags(): List<Tag> {
        return findAll<Tag>().toList()
    }
}