package me.ssttkkl.sharenote.controller

import me.ssttkkl.sharenote.controller.utils.userID
import me.ssttkkl.sharenote.model.view.PageView
import me.ssttkkl.sharenote.model.view.TagView
import me.ssttkkl.sharenote.service.TagService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class TagController(val tagService: TagService) {

    @GetMapping("/notes/all/tags")
    fun getUserTags(
        authentication: Authentication,
        @PageableDefault(page = 0, size = 20)
        @SortDefault.SortDefaults(
            SortDefault(sort = arrayOf("id"), direction = Sort.Direction.ASC)
        ) pageable: Pageable
    ): ResponseEntity<PageView<TagView>> {
        return ResponseEntity.ok(tagService.getUserTags(authentication.userID, pageable))
    }
}