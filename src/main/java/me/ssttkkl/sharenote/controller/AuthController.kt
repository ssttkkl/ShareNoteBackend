package me.ssttkkl.sharenote.controller

import me.ssttkkl.sharenote.model.view.Authentication
import me.ssttkkl.sharenote.model.view.UserView
import me.ssttkkl.sharenote.payload.LoginPayload
import me.ssttkkl.sharenote.payload.RefreshPayload
import me.ssttkkl.sharenote.payload.RegisterPayload
import me.ssttkkl.sharenote.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class AuthController(
    val authService: AuthService
) {

    @PostMapping("/auth/login")
    fun login(
        @RequestBody body: LoginPayload
    ): ResponseEntity<Authentication> {
        try {
            val auth = authService.login(body)
            return ResponseEntity.ok(auth)
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/auth/refresh")
    fun refresh(
        @RequestBody body: RefreshPayload
    ): ResponseEntity<Authentication> {
        try {
            val auth = authService.refresh(body)
            return ResponseEntity.ok(auth)
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/auth/register")
    fun register(
        @RequestBody body: RegisterPayload
    ): ResponseEntity<UserView> {
        val user = authService.register(body)
        return ResponseEntity.ok(user)
    }
}
