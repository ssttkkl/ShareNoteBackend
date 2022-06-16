package me.ssttkkl.sharenote.model.view

import me.ssttkkl.sharenote.model.entity.User
import java.io.Serializable

data class UserView(
    val id: Int,
    val username: String,
    val nickname: String
) : Serializable

fun User.toView() = UserView(id, username, nickname)