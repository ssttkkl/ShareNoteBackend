package me.ssttkkl.sharenote.component

import me.ssttkkl.sharenote.model.entity.RefreshToken
import me.ssttkkl.sharenote.model.entity.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Instant

@Component
class RefreshTokenGenerator {

    @Value("\${auth.refresh-token.expires-in}")
    var refreshExpiresIn: Long = 3600

    private val random = SecureRandom()

    fun generate(user: User): RefreshToken {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)

        val tokenValue = buildString {
            for (i in 0 until 32) {
                append(bytes[i].toUByte().toString(16).padStart(2, '0'))
            }
        }

        val now = Instant.now()
        return RefreshToken(
            tokenValue = tokenValue,
            user = user,
            createdAt = now,
            expiresAt = if (refreshExpiresIn > 0) now.plusSeconds(refreshExpiresIn) else null
        )
    }
}