package me.ssttkkl.sharenote.model.entity

import org.hibernate.Hibernate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "user")
@Access(AccessType.FIELD)
data class User(
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "user_id", nullable = false, updatable = false)
    @get:Access(AccessType.PROPERTY)
    var id: Int = 0,

    @get:JvmName("username")
    @Column(name = "username", nullable = false, updatable = false, length = 24)
    val username: String = "",

    @Column(name = "passwd", nullable = false, length = 100)
    var passwd: String = "",

    @Column(name = "nickname", nullable = false, length = 100)
    var nickname: String = "",

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "modified_at", nullable = false)
    var modifiedAt: Instant = Instant.now()
) : UserDetails {

    override fun getUsername(): String = username

    override fun getPassword(): String = passwd

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority("ROLE_USER"))

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    @OneToMany(mappedBy = "user")
    val tags: MutableSet<Tag> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}