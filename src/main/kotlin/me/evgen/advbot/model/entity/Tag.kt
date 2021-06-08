package me.evgen.advbot.model.entity

import me.evgen.advbot.db.TableName
import javax.persistence.*

@Entity
@Table(name = TableName.TAG)
class Tag() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    @Column(name ="name")
    lateinit var name: String

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = TableName.PLATFORM_TAG,
        joinColumns = [JoinColumn(name = "tag_id")],
        inverseJoinColumns = [JoinColumn(name = "platform_id")]
    )
    lateinit var platforms: Set<Platform>
}