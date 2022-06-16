package me.ssttkkl.sharenote.model.entity

import org.hibernate.Hibernate
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.*

@Entity
@Table(name = "note_invite")
data class NoteInvite(
    @Id
    @Column(name = "invite_id", nullable = false, length = 36)
    val id: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_id", updatable = false)
    val note: Note,

    @Column(name = "readonly", nullable = false)
    var readonly: Boolean,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant
) {

    constructor(id: String, note: Note, readonly: Boolean, createdAt: Instant = Instant.now()) : this(
        id = id,
        note = note,
        readonly = readonly,
        createdAt = createdAt,
        expiresAt = createdAt.plus(3, ChronoUnit.DAYS)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as NoteInvite

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}

val NoteInvite.isExpired
    get() = expiresAt.isBefore(Instant.now())