package me.ssttkkl.sharenote.service

import me.ssttkkl.sharenote.model.entity.User
import me.ssttkkl.sharenote.model.view.Authentication
import me.ssttkkl.sharenote.model.view.toView
import me.ssttkkl.sharenote.repository.RefreshTokenRepository
import me.ssttkkl.sharenote.repository.findOrInsertByUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class JwtIssuer(
    val refreshTokenRepo: RefreshTokenRepository,
    val jwtEncoder: JwtEncoder
) {

    @Value("\${jwt.expires-in}")
    var expiresIn: Long = 3600

    @Value("\${jwt.issuer}")
    lateinit var issuer: String

    private fun genRefreshToken(user: User): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    fun issue(user: User): Authentication {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiresIn))
            .subject(user.id.toString())
            .claim("roles", user.authorities.joinToString(" ") { it.authority })
            .build()
        val accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
        val refreshToken = refreshTokenRepo.findOrInsertByUser(user) { genRefreshToken(user) }.tokenValue
        return Authentication(accessToken, refreshToken, expiresIn, user.toView())
    }
}