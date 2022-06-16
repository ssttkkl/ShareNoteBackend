package me.ssttkkl.sharenote.repository;

import me.ssttkkl.sharenote.model.entity.RefreshToken
import me.ssttkkl.sharenote.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
    fun findByUserId(userID: Int): RefreshToken?

    @Modifying
    fun deleteByUserId(userID: Int): Int
}

fun RefreshTokenRepository.findOrInsertByUser(user: User, gen: () -> RefreshToken): RefreshToken {
    findByUserId(user.id)?.let { return it }

    var token: RefreshToken
    while (true) {
        token = gen()
        if (findById(token.tokenValue).isEmpty) {
            return save(token)
        }
    }
}