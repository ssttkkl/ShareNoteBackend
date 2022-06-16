package me.ssttkkl.sharenote.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.ssttkkl.sharenote.controller.utils.userID
import me.ssttkkl.sharenote.model.dto.NoteDto
import me.ssttkkl.sharenote.model.view.NoteView
import me.ssttkkl.sharenote.model.view.PageView
import me.ssttkkl.sharenote.service.NoteService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
class NoteController(
    val noteService: NoteService,
) {

    private val jackson = jacksonObjectMapper()

    @GetMapping("/notes")
    fun getUserNotes(
        authentication: Authentication,
        @RequestParam(defaultValue = "[]") titleKeywords: String,
        @RequestParam(defaultValue = "[]") tags: String,
        @RequestParam(defaultValue = "false") readonly: Boolean,
        @PageableDefault(page = 0, size = 20)
        @SortDefault.SortDefaults(
            SortDefault(sort = arrayOf("id"), direction = Sort.Direction.ASC)
        ) pageable: Pageable
    ): ResponseEntity<PageView<NoteView>> {
        val parsedTitleKeywords = jackson.readValue<Set<String>>(titleKeywords)
        val parsedTags = jackson.readValue<Set<Int>>(tags)
        val notes = noteService.getUserNotes(parsedTitleKeywords, parsedTags, readonly, authentication.userID, pageable)
        return ResponseEntity.ok(notes)
    }

    @GetMapping("/notes/{noteID}")
    fun getNoteByID(
        authentication: Authentication,
        @PathVariable noteID: Int,
    ): ResponseEntity<NoteView> {
        val note = noteService.getNoteByID(noteID, authentication.userID)
        return ResponseEntity.ok(note)
    }

    @GetMapping("notes/{noteID}/versions/{version}")
    fun getNoteVersionByID(
        authentication: Authentication,
        @PathVariable noteID: Int,
        @PathVariable version: Int,
    ): ResponseEntity<NoteView> {
        val note = noteService.getNoteByID(noteID, authentication.userID, version)
        return ResponseEntity.ok(note)
    }

    @GetMapping("notes/{noteID}/versions")
    fun getNoteAllVersionsByID(
        authentication: Authentication,
        @PathVariable noteID: Int,
        @PageableDefault(page = 0, size = 20)
        @SortDefault.SortDefaults(
            SortDefault(sort = arrayOf("version"), direction = Sort.Direction.DESC)
        ) pageable: Pageable
    ): ResponseEntity<PageView<NoteView>> {
        val notes = noteService.getNoteAllVersionsByID(noteID, authentication.userID, pageable)
        return ResponseEntity.ok(notes)
    }

    @PostMapping("/notes")
    fun createNote(
        authentication: Authentication,
        @RequestBody dto: NoteDto,
    ): ResponseEntity<NoteView> {
        val note = noteService.createNote(dto, authentication.userID)
        return ResponseEntity.ok(note)
    }

    @PutMapping("/notes/{noteID}")
    fun modifyNote(
        authentication: Authentication,
        @RequestBody dto: NoteDto,
        @PathVariable noteID: Int,
        @RequestParam ignoreConflict: Boolean,
    ): ResponseEntity<NoteView> {
        val note = noteService.modifyNote(noteID, dto, authentication.userID, ignoreConflict)
        return ResponseEntity.ok(note)
    }

    @DeleteMapping("/notes/{noteID}")
    fun deleteNote(
        authentication: Authentication,
        @PathVariable noteID: Int,
    ): ResponseEntity<Nothing?> {
        noteService.deleteNote(noteID, authentication.userID)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/notes/{noteID}/tags")
    fun setNoteTags(
        authentication: Authentication,
        @PathVariable noteID: Int,
        @RequestBody tags: Set<String>,
    ): ResponseEntity<Nothing?> {
        noteService.setNoteTags(noteID, tags, authentication.userID)
        return ResponseEntity.ok().build()
    }
}