package me.ssttkkl.sharenote.model.entity

import org.hibernate.Hibernate
import java.io.Serializable
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "note_permission")
data class NotePermission(
    @EmbeddedId
    val id: PrimaryKey,

    @MapsId("noteId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", updatable = false)
    val note: Note,

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    val user: User,

    @Column(name = "readonly", nullable = false)
    var readonly: Boolean = false,

    @Enumerated
    @Column(name = "state", nullable = false)
    var state: State = State.Active,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    @ManyToOne
    @JoinColumn(name = "deleted_at_version_id")
    var deletedAtVersion: NoteVersion? = null
) {

    enum class State {
        Active, DeletedBySelf, DeletedByOwner
    }

    constructor(note: Note, user: User, readonly: Boolean = false) : this(
        PrimaryKey(note.id, user.id),
        note,
        user,
        readonly
    )

    @Embeddable
    data class PrimaryKey(
        @Column(name = "note_id", nullable = false)
        val noteID: Int,

        @Column(name = "user_id", nullable = false)
        val userID: Int
    ) : Serializable {

        override fun hashCode(): Int = Objects.hash(noteID, userID)
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

            other as PrimaryKey

            return noteID == other.noteID &&
                    userID == other.userID
        }

        companion object {
            private const val serialVersionUID = 7793242383803104401L
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as NotePermission

        return id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id);

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(noteID = ${id.noteID}, userID = ${id.userID})"
    }
}

val NotePermission.isDeleted
    get() = state == NotePermission.State.DeletedByOwner || state == NotePermission.State.DeletedBySelf