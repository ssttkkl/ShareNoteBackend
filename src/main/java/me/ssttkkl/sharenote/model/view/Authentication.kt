package me.ssttkkl.sharenote.model.view

import java.io.Serializable

data class Authentication(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserView
) : Serializable