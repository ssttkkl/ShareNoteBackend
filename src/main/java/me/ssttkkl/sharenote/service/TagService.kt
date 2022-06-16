package me.ssttkkl.sharenote.service

import me.ssttkkl.sharenote.model.view.PageView
import me.ssttkkl.sharenote.model.view.TagView
import me.ssttkkl.sharenote.model.view.toView
import me.ssttkkl.sharenote.repository.TagRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService(
    val tagRepo: TagRepository,
) {

    @Transactional
    fun getUserTags(userID: Int, pageable: Pageable): PageView<TagView> {
        return tagRepo.countByUserId(userID, pageable).map { TagView(it.first.tagName, it.second) }.toView()
    }
}