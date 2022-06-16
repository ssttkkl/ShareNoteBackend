package me.ssttkkl.sharenote.component

import me.ssttkkl.sharenote.model.entity.User
import me.ssttkkl.sharenote.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtIssuer(
    val jwtEncoder: JwtEncoder
) {

    @Value("\${auth.jwt.expires-in}")
    var expiresIn: Long = 3600

    @Value("\${auth.jwt.issuer}")
    lateinit var issuer: String

    fun issue(user: User): Jwt {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiresIn))
            .subject(user.id.toString())
            .claim("roles", user.authorities.joinToString(" ") { it.authority })
            .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
    }
}