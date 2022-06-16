package me.ssttkkl.sharenote.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

sealed class NotePermissionException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotePermissionNotFoundException : NotePermissionException("Note permission not found")

@ResponseStatus(HttpStatus.FORBIDDEN)
class ModifyOwnerNotePermissionException : NotePermissionException("Cannot modify owner's permission")