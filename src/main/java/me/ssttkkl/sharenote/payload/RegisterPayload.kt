package me.ssttkkl.sharenote.payload


data class RegisterPayload(
    val username: String,
    val password: String,
    val nickname: String,
)