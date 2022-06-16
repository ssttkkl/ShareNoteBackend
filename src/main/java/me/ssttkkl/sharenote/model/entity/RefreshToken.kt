package me.ssttkkl.sharenote.model.entity

import org.hibernate.Hibernate
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "refresh_token")
data class RefreshToken(
    @Id
    @Column(name = "token", nullable = false, updatable = false, length = 24)
    val tokenValue: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val user: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,

    @Column(name = "expires_at")
    val expiresAt: Instant? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as RefreshToken

        return tokenValue == other.tokenValue
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(token = $tokenValue )"
    }
}