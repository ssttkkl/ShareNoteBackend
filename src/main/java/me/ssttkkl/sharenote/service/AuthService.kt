package me.ssttkkl.sharenote.service

import me.ssttkkl.sharenote.component.JwtIssuer
import me.ssttkkl.sharenote.component.RefreshTokenGenerator
import me.ssttkkl.sharenote.exception.RefreshTokenException
import me.ssttkkl.sharenote.exception.UserAlreadyExistsException
import me.ssttkkl.sharenote.model.entity.User
import me.ssttkkl.sharenote.model.view.Authentication
import me.ssttkkl.sharenote.model.view.UserView
import me.ssttkkl.sharenote.model.view.toView
import me.ssttkkl.sharenote.payload.LoginPayload
import me.ssttkkl.sharenote.payload.RefreshPayload
import me.ssttkkl.sharenote.payload.RegisterPayload
import me.ssttkkl.sharenote.repository.RefreshTokenRepository
import me.ssttkkl.sharenote.repository.UserRepository
import me.ssttkkl.sharenote.repository.findOrInsertByUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    val userRepo: UserRepository,
    val refreshTokenRepo: RefreshTokenRepository,
    val refreshTokenGenerator: RefreshTokenGenerator,
) : UserDetailsService {

    @Autowired
    @org.springframework.context.annotation.Lazy
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    @org.springframework.context.annotation.Lazy
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    @org.springframework.context.annotation.Lazy
    lateinit var jwtIssuer: JwtIssuer

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepo.findByUsername(username) ?: throw UsernameNotFoundException("user not found")
    }

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

    @Transactional
    fun register(payload: RegisterPayload): UserView {
        val user = userRepo.findByUsername(payload.username)
        if (user != null)
            throw UserAlreadyExistsException(payload.username)

        return userRepo.save(
            User(
                username = payload.username,
                passwd = passwordEncoder.encode(payload.password),
                nickname = payload.nickname,
            )
        ).toView()
    }
}