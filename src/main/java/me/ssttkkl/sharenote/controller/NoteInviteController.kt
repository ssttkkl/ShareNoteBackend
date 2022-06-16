package me.ssttkkl.sharenote.controller

import me.ssttkkl.sharenote.controller.utils.userID
import me.ssttkkl.sharenote.model.view.NoteInviteView
import me.ssttkkl.sharenote.service.NoteInviteService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
class NoteInviteController(
    val inviteService: NoteInviteService
) {

    @PostMapping("/invites")
    fun createInvite(
        authentication: Authentication,
        @RequestParam noteID: Int,
        @RequestParam(defaultValue = "false") readonly: Boolean
    ): ResponseEntity<NoteInviteView> {
        return ResponseEntity.ok(inviteService.createInvite(noteID, authentication.userID, readonly))
    }

    @GetMapping("/invites/{inviteID}")
    fun getInvite(
        @PathVariable inviteID: String,
    ): ResponseEntity<NoteInviteView> {
        return ResponseEntity.ok(inviteService.getInvite(inviteID))
    }

    @PostMapping("/invites/{inviteID}/consumption")
    fun consumeInvite(
        authentication: Authentication,
        @PathVariable inviteID: String,
    ): ResponseEntity<Nothing?> {
        inviteService.consumeInvite(inviteID, authentication.userID)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/invites/{inviteID}")
    fun invalidateInvite(
        authentication: Authentication,
        @PathVariable inviteID: String,
    ): ResponseEntity<Nothing?> {
        inviteService.invalidateInvite(inviteID, authentication.userID)
        return ResponseEntity.ok().build()
    }
}