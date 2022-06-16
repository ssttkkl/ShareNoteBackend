package me.ssttkkl.sharenote.model.view

import me.ssttkkl.sharenote.model.entity.NotePermission
import java.io.Serializable
import java.time.Instant

data class NotePermissionView(
    val readonly: Boolean,
    val createdAt: Instant,
    val deletedAt: Instant?,
) : Serializable

fun NotePermission.toView() = NotePermissionView(
    readonly = readonly,
    createdAt = createdAt,
    deletedAt = deletedAt
)

data class NotePermissionWithUserView(
    val user: UserView,
    val readonly: Boolean,
    val createdAt: Instant,
    val deletedAt: Instant?,
) : Serializable

fun NotePermission.toViewWithUser() = NotePermissionWithUserView(
    user = user.toView(),
    readonly = readonly,
    createdAt = createdAt,
    deletedAt = deletedAt
)
