package me.evgen.advbot.service

import me.evgen.advbot.db.dao.TagsDaoImpl
import me.evgen.advbot.model.entity.Tag

object TagService {
    private val tagDao = TagsDaoImpl()

    fun getTag(id: Long): Tag? {
        return tagDao.findTag(id)
    }

    fun getAllTags(): List<Tag> {
        return tagDao.findAllTags()
    }
}