package me.ssttkkl.sharenote.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

sealed class NoteException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class NoteNotFoundException : NoteException("Note not found")

@ResponseStatus(HttpStatus.FORBIDDEN)
class NoteForbiddenException : NoteException("Forbidden")

@ResponseStatus(HttpStatus.CONFLICT)
class NoteVersionConflictException : NoteException("Version conflict")