package me.ssttkkl.sharenote.model.dto

data class LoginPayload(
    val username: String,
    val password: String
)

data class RefreshPayload(
    val refreshToken: String
    )