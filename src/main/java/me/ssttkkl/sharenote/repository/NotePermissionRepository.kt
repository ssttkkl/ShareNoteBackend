package me.ssttkkl.sharenote.repository

import me.ssttkkl.sharenote.model.entity.NotePermission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NotePermissionRepository : JpaRepository<NotePermission, NotePermission.PrimaryKey> {
    @Query("select np from NotePermission np where np.id.noteID = ?1 and np.deletedAt is null and np.note.owner.id <> np.user.id")
    fun findByNoteIdExcludingOwner(noteID: Int, pageable: Pageable): Page<NotePermission>
}