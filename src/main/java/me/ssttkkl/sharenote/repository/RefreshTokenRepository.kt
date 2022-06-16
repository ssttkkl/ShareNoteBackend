package me.ssttkkl.sharenote.repository;

import me.ssttkkl.sharenote.model.entity.RefreshToken
import me.ssttkkl.sharenote.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
    fun findByUserId(userID: Int): RefreshToken?
}

fun RefreshTokenRepository.findOrInsertByUser(user: User, gen: () -> String): RefreshToken {
    val token = findByUserId(user.id)
    if (token != null)
        return token
    else {
        return save(RefreshToken(gen(), user))
    }
}