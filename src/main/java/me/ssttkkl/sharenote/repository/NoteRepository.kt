package me.ssttkkl.sharenote.repository;

import me.ssttkkl.sharenote.model.entity.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository : JpaRepository<Note, Int>, JpaSpecificationExecutor<Note>
