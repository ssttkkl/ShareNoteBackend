package me.ssttkkl.sharenote.model.view

import me.ssttkkl.sharenote.model.entity.Note

data class NoteSummary(
    val id: Int = 0,
    val title: String,
    val ownerUser: UserView,
)

fun Note.toNoteSummary() = NoteSummary(
    id = id,
    title = latestVersion?.title ?: error("no version"),
    ownerUser = ownerUser.toView()
)