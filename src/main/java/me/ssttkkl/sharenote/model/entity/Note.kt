package me.ssttkkl.sharenote.model.entity

import org.hibernate.Hibernate
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "note")
@Access(AccessType.FIELD)
data class Note(
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "note_id", nullable = false, updatable = false)
    @get:Access(AccessType.PROPERTY)
    var id: Int = 0,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.DETACH])
    @JoinTable(
        name = "note_tag",
        joinColumns = [JoinColumn(name = "note_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableSet<Tag> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "latest_version_id")
    var latestVersion: NoteVersion? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false, updatable = false)
    val ownerUser: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,
) {

    @OneToMany(
        mappedBy = "note",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE],
        orphanRemoval = true
    )
    val versions: MutableSet<NoteVersion> = mutableSetOf()

    @OneToMany(
        mappedBy = "note",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE],
        orphanRemoval = true
    )
    val invites: MutableSet<NoteInvite> = mutableSetOf()

    @OneToMany(
        mappedBy = "note",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE],
        orphanRemoval = true
    )
    val permissions: MutableSet<NotePermission> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Note

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}

val Note.isDeleted get() = deletedAt != null