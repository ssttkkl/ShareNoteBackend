package me.ssttkkl.sharenote.service

import me.ssttkkl.sharenote.exception.ModifyOwnerNotePermissionException
import me.ssttkkl.sharenote.exception.NoteForbiddenException
import me.ssttkkl.sharenote.exception.NoteNotFoundException
import me.ssttkkl.sharenote.exception.NotePermissionNotFoundException
import me.ssttkkl.sharenote.model.dto.NotePermissionDto
import me.ssttkkl.sharenote.model.entity.NotePermission
import me.ssttkkl.sharenote.model.entity.isDeleted
import me.ssttkkl.sharenote.model.view.NotePermissionWithUserView
import me.ssttkkl.sharenote.model.view.PageView
import me.ssttkkl.sharenote.model.view.toView
import me.ssttkkl.sharenote.model.view.toViewWithUser
import me.ssttkkl.sharenote.repository.NotePermissionRepository
import me.ssttkkl.sharenote.repository.NoteRepository
import me.ssttkkl.sharenote.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class NotePermissionService(
    val permissionRepo: NotePermissionRepository,
    val noteRepo: NoteRepository,
    val userRepo: UserRepository,
) {
    @Transactional
    fun getNotePermissions(noteID: Int, userID: Int, pageable: Pageable): PageView<NotePermissionWithUserView> {
        val note = noteRepo.findById(noteID).orElseThrow { NoteNotFoundException() }
        if (note.isDeleted)
            throw NoteNotFoundException()
        if (note.owner.id != userID)
            throw NoteForbiddenException()
        return permissionRepo.findByNoteIdExcludingOwner(noteID, pageable).map { it.toViewWithUser() }.toView()
    }

    @Transactional
    fun putNotePermission(dto: NotePermissionDto, operatorUserID: Int) {
        val note = noteRepo.findById(dto.noteID).orElseThrow { NoteNotFoundException() }
        if (note.isDeleted)
            throw NoteNotFoundException()

        // owner can modify any permission except self's
        if (dto.userID == operatorUserID)
            throw ModifyOwnerNotePermissionException()
        if (note.owner.id != operatorUserID)
            throw NoteForbiddenException()

        val permission = NotePermission(
            note = note,
            user = userRepo.getReferenceById(dto.userID),
            readonly = dto.readonly
        )
        permissionRepo.save(permission)
    }

    @Transactional
    fun deleteNotePermission(id: NotePermission.PrimaryKey, operatorUserID: Int) {
        val note = noteRepo.findById(id.noteID).orElseThrow { NoteNotFoundException() }
        if (note.isDeleted)
            throw NoteNotFoundException()

        if (operatorUserID == note.owner.id) {
            if (id.userID == operatorUserID) {
                // owner can modify any permission except self's
                throw ModifyOwnerNotePermissionException()
            }
        } else {
            if (id.userID != operatorUserID) {
                // other user can only modify self's
                throw NoteForbiddenException()
            }
        }

        val permission = permissionRepo.findById(id).orElseThrow { NotePermissionNotFoundException() }
        if (permission.state == NotePermission.State.DeletedBySelf)
            throw NotePermissionNotFoundException()

        permission.deletedAt = Instant.now()
        permission.deletedAtVersion = note.latestVersion
        permission.state = NotePermission.State.DeletedByOwner
        permissionRepo.save(permission)
    }

    @Transactional
    fun deleteSelfNotePermission(id: NotePermission.PrimaryKey) {
        val note = noteRepo.findById(id.noteID).orElseThrow { NoteNotFoundException() }

        if (id.userID == note.owner.id) {
            // owner cannot modify self's permission
            throw ModifyOwnerNotePermissionException()
        }

        val permission = permissionRepo.findById(id).orElseThrow { NotePermissionNotFoundException() }
        if (permission.state == NotePermission.State.DeletedBySelf)
            throw NotePermissionNotFoundException()

        permission.deletedAt = permission.deletedAt ?: Instant.now()  // in case that it was deleted by owner
        permission.deletedAtVersion = permission.deletedAtVersion ?: note.latestVersion
        permission.state = NotePermission.State.DeletedBySelf
        permissionRepo.save(permission)

        // delete user's all tag
        note.tags.removeIf {
            it.user.id == id.userID
        }
    }
}