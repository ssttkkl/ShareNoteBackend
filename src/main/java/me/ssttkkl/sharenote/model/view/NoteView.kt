package me.ssttkkl.sharenote.model.view

import me.ssttkkl.sharenote.exception.NoteForbiddenException
import me.ssttkkl.sharenote.model.entity.Note
import me.ssttkkl.sharenote.model.entity.NoteVersion
import me.ssttkkl.sharenote.model.entity.isDeleted
import java.io.Serializable
import java.time.Instant

data class NoteView(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val tags: Set<String> = emptySet(),
    val version: Int = 0,
    val ownerUser: UserView,
    val modifiedBy: UserView,
    val permission: NotePermissionView,
    val createdAt: Instant,
    val modifiedAt: Instant,
) : Serializable

fun Note.toView(uid: Int, version: NoteVersion? = null): NoteView {
    val permission = permissions.find { it.user.id == uid } ?: throw NoteForbiddenException()

    val version = version
        ?: (if (permission.isDeleted)
            permission.deletedAtVersion
        else
            latestVersion)
        ?: error("no version")

    return NoteView(
        id = id,
        title = version.title,
        content = version.content,
        tags = tags.filter { it.user.id == uid }.mapTo(mutableSetOf()) { it.tagName },
        ownerUser = ownerUser.toView(),
        modifiedBy = version.creator.toView(),
        permission = permission.toView(),
        version = version.version,
        createdAt = createdAt,
        modifiedAt = version.createdAt,
    )
}