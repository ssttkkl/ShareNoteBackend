package me.ssttkkl.sharenote.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

sealed class UserException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException() : UserException("User not found")

@ResponseStatus(HttpStatus.FORBIDDEN)
class UserForbiddenException() : UserException("User forbidden")

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UserIncorrectPasswordException() : UserException("User password incorrect")

@ResponseStatus(HttpStatus.CONFLICT)
class UserAlreadyExistsException(username: String) : UserException("User $username already exists")