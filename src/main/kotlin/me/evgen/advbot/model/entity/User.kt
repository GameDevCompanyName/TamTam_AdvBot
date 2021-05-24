package me.evgen.advbot.model.entity

import com.google.gson.Gson
import me.evgen.advbot.db.TableName
import me.evgen.advbot.model.navigation.Payload
import me.evgen.advbot.model.state.BaseState
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = TableName.USER)
class User() {
    @Id
    var id: Long = 0
    lateinit var payload: String

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
    lateinit var advertList: List<Advert>

    constructor(id: Long, currentState: BaseState) : this() {
        this.id = id
        this.payload = currentState.toPayload().toJson()
    }

    fun getState(): BaseState {
        val gson = Gson()
        val payload = gson.fromJson(payload, Payload::class.java)

        return gson.fromJson(payload.jsonState, Class.forName(payload.className)) as BaseState
    }
}
