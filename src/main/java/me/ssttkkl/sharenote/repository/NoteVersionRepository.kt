package me.ssttkkl.sharenote.repository;

import me.ssttkkl.sharenote.model.entity.NoteVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NoteVersionRepository : JpaRepository<NoteVersion, Int> {
    fun findByNoteId(noteID: Int, pageable: Pageable): Page<NoteVersion>

    fun findByNoteIdAndVersion(noteID: Int, version: Int): NoteVersion?
}