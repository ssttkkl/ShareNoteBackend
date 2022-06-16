package me.ssttkkl.sharenote.controller

import me.ssttkkl.sharenote.controller.utils.userID
import me.ssttkkl.sharenote.model.dto.NotePermissionDto
import me.ssttkkl.sharenote.model.entity.NotePermission
import me.ssttkkl.sharenote.model.view.NotePermissionWithUserView
import me.ssttkkl.sharenote.model.view.PageView
import me.ssttkkl.sharenote.service.NotePermissionService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class NotePermissionController(
    val permissionService: NotePermissionService
) {

    @GetMapping("/notes/{noteID}/permissions")
    fun getNotePermissions(
        authentication: Authentication,
        @PathVariable noteID: Int,
        @PageableDefault(page = 0, size = 20)
        @SortDefault.SortDefaults(
            SortDefault(sort = arrayOf("id"), direction = Sort.Direction.ASC)
        ) pageable: Pageable
    ): ResponseEntity<PageView<NotePermissionWithUserView>> {
        return ResponseEntity.ok(permissionService.getNotePermissions(noteID, authentication.userID, pageable))
    }

    @PutMapping("/notes/{noteID}/permissions/{userID}")
    fun modifyNotePermission(
        authentication: Authentication,
        @PathVariable noteID: Int,
        @PathVariable userID: Int,
        @RequestParam readonly: Boolean,
    ): ResponseEntity<Nothing?> {
        permissionService.putNotePermission(
            NotePermissionDto(noteID, userID, readonly),
            authentication.userID
        )
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/notes/{noteID}/permissions/{userID}")
    fun deleteNotePermission(
        authentication: Authentication,
        @PathVariable noteID: Int,
        @PathVariable userID: Int,
    ): ResponseEntity<Nothing?> {
        permissionService.deleteNotePermission(
            NotePermission.PrimaryKey(noteID, userID),
            authentication.userID
        )
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/notes/{noteID}/permissions/self")
    fun deleteSelfNotePermission(
        authentication: Authentication,
        @PathVariable noteID: Int
    ): ResponseEntity<Nothing?> {
        permissionService.deleteSelfNotePermission(
            NotePermission.PrimaryKey(noteID, authentication.userID)
        )
        return ResponseEntity.ok().build()
    }
}