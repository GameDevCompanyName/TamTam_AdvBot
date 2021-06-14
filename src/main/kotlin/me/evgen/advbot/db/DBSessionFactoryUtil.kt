package me.evgen.advbot.db

import me.evgen.advbot.model.entity.*
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration


object DBSessionFactoryUtil {
    val sessionFactory: SessionFactory = initSessionFactory()

    private val tags = listOf(
        Tag().apply { id = 1; name = "СПОРТ" },
        Tag().apply { id = 2; name = "НОВОСТИ" },
        Tag().apply { id = 3; name = "ИГРЫ" },
        Tag().apply { id = 4; name = "ПОЛИТИКА" },
        Tag().apply { id = 5; name = "ТЕХНОЛОГИИ" },
        Tag().apply { id = 6; name = "ЭКОНОМИКА" }
    )

    init {
        sessionFactory.openSession().apply {
            beginTransaction().apply {
                for (t in tags) {
                    saveOrUpdate(t)
                }
                commit()
            }
        }
    }

    //TODO try catch init
    private fun initSessionFactory(): SessionFactory {
        val configuration = Configuration().configure().apply {
            addAnnotatedClass(Advert::class.java)
            addAnnotatedClass(User::class.java)
            addAnnotatedClass(Platform::class.java)
            addAnnotatedClass(Tag::class.java)
            addAnnotatedClass(AdvertTag::class.java)
            addAnnotatedClass(PlatformTag::class.java)
            addAnnotatedClass(Campaign::class.java)
        }
        val builder = StandardServiceRegistryBuilder().applySettings(configuration.properties)

        return configuration.buildSessionFactory(builder.build())
    }
}