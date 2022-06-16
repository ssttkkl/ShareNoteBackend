package me.ssttkkl.sharenote.model.dto

import java.io.Serializable

data class NoteDto(
    val id: Int = 0,
    val title: String,
    val content: String,
    val version: Int = 0,
    val readonly: Boolean? = null,
) : Serializable