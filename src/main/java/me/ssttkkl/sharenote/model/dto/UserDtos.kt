package me.ssttkkl.sharenote.model.dto


data class RegisterPayload(
    val username: String,
    val password: String,
    val nickname: String,
)

data class ModifyUserPayload(
    val oldPassword: String,
    val newPassword: String?,
    val nickname: String?,
)