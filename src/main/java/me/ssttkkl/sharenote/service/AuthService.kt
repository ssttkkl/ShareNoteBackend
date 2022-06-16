package me.ssttkkl.sharenote.service

import me.ssttkkl.sharenote.component.JwtIssuer
import me.ssttkkl.sharenote.component.RefreshTokenGenerator
import me.ssttkkl.sharenote.exception.RefreshTokenException
import me.ssttkkl.sharenote.model.entity.User
import me.ssttkkl.sharenote.model.view.Authentication
import me.ssttkkl.sharenote.model.view.toView
import me.ssttkkl.sharenote.model.dto.LoginPayload
import me.ssttkkl.sharenote.model.dto.RefreshPayload
import me.ssttkkl.sharenote.repository.RefreshTokenRepository
import me.ssttkkl.sharenote.repository.findOrInsertByUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthService(
    val refreshTokenRepo: RefreshTokenRepository,
    val refreshTokenGenerator: RefreshTokenGenerator,
) {

    @Autowired
    @org.springframework.context.annotation.Lazy
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    @org.springframework.context.annotation.Lazy
    lateinit var jwtIssuer: JwtIssuer

    fun login(payload: LoginPayload): Authentication {
        val authentication = authenticationManager
            .authenticate(UsernamePasswordAuthenticationToken(payload.username, payload.password))
        SecurityContextHolder.getContext().authentication = authentication

        val user = authentication.principal as User
        val accessToken = jwtIssuer.issue(user)
        val refreshToken = refreshTokenRepo.findOrInsertByUser(user) { refreshTokenGenerator.generate(user) }
        return Authentication(accessToken.tokenValue, refreshToken.tokenValue, accessToken.expiresAt!!, user.toView())
    }

    fun refresh(payload: RefreshPayload): Authentication {
        val refreshToken = refreshTokenRepo.findById(payload.refreshToken)
            .orElseThrow { RefreshTokenException() }
        val accessToken = jwtIssuer.issue(refreshToken.user)
        return Authentication(
            accessToken.tokenValue,
            refreshToken.tokenValue,
            accessToken.expiresAt!!,
            refreshToken.user.toView()
        )
    }
}