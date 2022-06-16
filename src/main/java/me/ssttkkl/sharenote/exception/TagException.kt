package me.ssttkkl.sharenote.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

sealed class TagException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class TagNotFoundException : TagException("Tag not found")

@ResponseStatus(HttpStatus.FORBIDDEN)
class TagForbiddenException : TagException("Tag forbidden")