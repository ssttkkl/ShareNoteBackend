package me.ssttkkl.sharenote.model.entity

import org.hibernate.Hibernate
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "note_version")
@Access(AccessType.FIELD)
data class NoteVersion(
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "note_version_id", nullable = false, updatable = false)
    @get:Access(AccessType.PROPERTY)
    var id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_id", nullable = false, updatable = false)
    val note: Note,

    @Column(name = "version", nullable = false, updatable = false)
    val version: Int = 1,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_user_id", nullable = false, updatable = false)
    val creator: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "title", nullable = false, updatable = false, length = 100)
    val title: String = "",

    @Lob
    @Column(name = "content", nullable = false, updatable = false)
    val content: String = "",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as NoteVersion

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}