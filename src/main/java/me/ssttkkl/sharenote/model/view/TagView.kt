package me.ssttkkl.sharenote.model.view

import java.io.Serializable

data class TagView(
    val tagName: String,
    val count: Long,
) : Serializable
