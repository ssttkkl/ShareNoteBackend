package me.ssttkkl.sharenote.model.dto

import java.io.Serializable

data class NotePermissionDto(
    val noteID: Int,
    val userID: Int,
    val readonly: Boolean = false
) : Serializable