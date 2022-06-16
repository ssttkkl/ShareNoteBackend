package me.ssttkkl.sharenote.model.entity

import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "tag")
@Access(AccessType.FIELD)
data class Tag(
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "tag_id", nullable = false, updatable = false)
    @get:Access(AccessType.PROPERTY)
    var id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val user: User,

    @Column(name = "tag_name", nullable = false, updatable = false, length = 100)
    val tagName: String = "",
) {

    @ManyToMany(mappedBy = "tags")
    val notes: MutableSet<Note> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Tag

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "Tag(id=$id, userID=${user.id}, tagName='$tagName')"
    }
}