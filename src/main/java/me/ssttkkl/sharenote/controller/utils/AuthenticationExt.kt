package me.ssttkkl.sharenote.controller.utils

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt

val Authentication.userID: Int
    get() = (this.principal as Jwt).getClaim("id")