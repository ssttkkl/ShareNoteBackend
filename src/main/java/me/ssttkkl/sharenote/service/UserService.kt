package me.ssttkkl.sharenote.service

import me.ssttkkl.sharenote.exception.UserAlreadyExistsException
import me.ssttkkl.sharenote.exception.UserIncorrectPasswordException
import me.ssttkkl.sharenote.exception.UserNotFoundException
import me.ssttkkl.sharenote.model.dto.ModifyUserPayload
import me.ssttkkl.sharenote.model.dto.RegisterPayload
import me.ssttkkl.sharenote.model.entity.User
import me.ssttkkl.sharenote.model.view.UserView
import me.ssttkkl.sharenote.model.view.toView
import me.ssttkkl.sharenote.repository.RefreshTokenRepository
import me.ssttkkl.sharenote.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class UserService(
    val userRepo: UserRepository,
    val refreshTokenRepo: RefreshTokenRepository,
) : UserDetailsService {

    @Autowired
    @org.springframework.context.annotation.Lazy
    lateinit var passwordEncoder: PasswordEncoder

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepo.findByUsername(username) ?: throw UsernameNotFoundException("user not found")
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

    @Transactional
    fun modifyUserInfo(payload: ModifyUserPayload, userID: Int) {
        val user = userRepo.findById(userID).orElseThrow { UserNotFoundException() }

        if (user.passwd != passwordEncoder.encode(payload.oldPassword)) {
            throw UserIncorrectPasswordException()
        }

        if (payload.newPassword != null) {
            user.passwd = passwordEncoder.encode(payload.newPassword)
            refreshTokenRepo.deleteByUserId(userID) // delete his refresh token
        }
        if (payload.nickname != null) {
            user.nickname = payload.nickname
        }
        user.modifiedAt = Instant.now()

        userRepo.save(user)

    }
}