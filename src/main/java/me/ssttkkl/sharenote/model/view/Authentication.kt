package me.ssttkkl.sharenote.model.view

import java.io.Serializable
import java.time.Instant

data class Authentication(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Instant,
    val user: UserView
) : Serializable