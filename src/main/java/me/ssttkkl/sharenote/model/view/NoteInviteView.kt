package me.ssttkkl.sharenote.model.view

import me.ssttkkl.sharenote.model.entity.NoteInvite
import java.io.Serializable
import java.time.Instant

data class NoteInviteView(
    val id: String,
    val readonly: Boolean,
    val createdAt: Instant,
    val expiresAt: Instant,
    val note: NoteSummary?
) : Serializable

fun NoteInvite.toView(withSummary: Boolean = false) = NoteInviteView(
    id = id,
    readonly = readonly,
    createdAt = createdAt,
    expiresAt = expiresAt,
    note = if (withSummary) note.toNoteSummary() else null,
)