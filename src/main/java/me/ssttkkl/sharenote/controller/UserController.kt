package me.ssttkkl.sharenote.controller

import me.ssttkkl.sharenote.controller.utils.userID
import me.ssttkkl.sharenote.model.dto.ModifyUserPayload
import me.ssttkkl.sharenote.model.view.UserView
import me.ssttkkl.sharenote.model.dto.RegisterPayload
import me.ssttkkl.sharenote.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class UserController(
    val userService: UserService
) {

    @PostMapping("/users")
    fun register(
        @RequestBody body: RegisterPayload
    ): ResponseEntity<UserView> {
        val user = userService.register(body)
        return ResponseEntity.ok(user)
    }

    @PostMapping("/users/info")
    fun modifyUserInfo(
        authentication: Authentication,
        @RequestBody body: ModifyUserPayload
    ): ResponseEntity<Nothing?> {
        userService.modifyUserInfo(body, authentication.userID)
        return ResponseEntity(HttpStatus.OK)
    }
}