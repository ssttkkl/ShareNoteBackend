package me.ssttkkl.sharenote.model.view

import org.springframework.data.domain.Page
import java.io.Serializable

data class PageView<T>(
    val content: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long
) : Serializable

fun <T> Page<T>.toView() = PageView(
    content = this.content,
    page = this.number,
    pageSize = this.size,
    totalPages = this.totalPages,
    totalElements = this.totalElements
)