package me.ssttkkl.sharenote.service

import me.ssttkkl.sharenote.exception.NoteForbiddenException
import me.ssttkkl.sharenote.exception.NoteInviteNotFoundException
import me.ssttkkl.sharenote.exception.NoteNotFoundException
import me.ssttkkl.sharenote.model.entity.NoteInvite
import me.ssttkkl.sharenote.model.entity.NotePermission
import me.ssttkkl.sharenote.model.entity.isDeleted
import me.ssttkkl.sharenote.model.entity.isExpired
import me.ssttkkl.sharenote.model.view.NoteInviteView
import me.ssttkkl.sharenote.model.view.toView
import me.ssttkkl.sharenote.repository.NoteInviteRepository
import me.ssttkkl.sharenote.repository.NotePermissionRepository
import me.ssttkkl.sharenote.repository.NoteRepository
import me.ssttkkl.sharenote.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NoteInviteService(
    val inviteRepo: NoteInviteRepository,
    val noteRepo: NoteRepository,
    val notePermissionRepo: NotePermissionRepository,
    val userRepo: UserRepository
) {

    @Transactional
    fun createInvite(
        noteID: Int,
        userID: Int,
        readonly: Boolean
    ): NoteInviteView {
        val note = noteRepo.findById(noteID).orElseThrow { NoteNotFoundException() }
        if (note.isDeleted)
            throw NoteNotFoundException()
        if (note.ownerUser.id != userID)
            throw NoteForbiddenException()

        val invite = inviteRepo.save(
            NoteInvite(
                id = UUID.randomUUID().toString().replace("-", ""),
                note = note,
                readonly = readonly
            )
        )

        return invite.toView()
    }

    @Transactional
    fun getInvite(
        inviteID: String
    ): NoteInviteView {
        val invite = inviteRepo.findById(inviteID).orElseThrow { NoteInviteNotFoundException() }
        if (invite.isExpired || invite.note.isDeleted) {
            throw NoteInviteNotFoundException()
        }
        return invite.toView(withSummary = true)
    }

    @Transactional
    fun consumeInvite(
        inviteID: String,
        userID: Int
    ) {
        val invite = inviteRepo.findById(inviteID).orElseThrow { NoteInviteNotFoundException() }
        if (invite.isExpired || invite.note.isDeleted) {
            throw NoteInviteNotFoundException()
        }

        val notePermission = NotePermission(
            note = invite.note,
            user = userRepo.getReferenceById(userID),
            readonly = invite.readonly
        )
        notePermissionRepo.save(notePermission)
    }

    @Transactional
    fun invalidateInvite(
        inviteID: String,
        userID: Int
    ) {
        val invite = inviteRepo.findById(inviteID).orElseThrow { NoteInviteNotFoundException() }
        if (invite.isExpired || invite.note.isDeleted) {
            throw NoteInviteNotFoundException()
        }
        if (invite.note.ownerUser.id != userID) {
            throw NoteForbiddenException()
        }

        inviteRepo.delete(invite)
    }
}