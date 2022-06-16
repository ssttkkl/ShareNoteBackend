package me.ssttkkl.sharenote.repository

import me.ssttkkl.sharenote.model.entity.Tag
import me.ssttkkl.sharenote.model.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : JpaRepository<Tag, Int> {

    fun findByUserId(userID: Int, pageable: Pageable): Page<Tag>

    @Query(
        """
        select new kotlin.Pair(t, count(n))
        from Tag t
        join t.notes n
        where t.user.id = ?1
        group by t.id
    """
    )
    fun countByUserId(userID: Int, pageable: Pageable): Page<Pair<Tag, Long>>

    fun findByUserIdAndTagName(userID: Int, tagName: String): Tag?

    @Query("select t from Tag t where t.user.id = ?1 and t.tagName in ?2")
    fun findByUserIdAndTagName(userID: Int, tagName: Collection<String>): Set<Tag>
}

fun TagRepository.findOrInsertByUserAndTagName(user: User, tagName: String): Tag {
    return findByUserIdAndTagName(user.id, tagName) ?: save(Tag(user = user, tagName = tagName))
}

fun TagRepository.findOrInsertByUserAndTagName(user: User, tagName: Collection<String>): Set<Tag> {
    val existing = findByUserIdAndTagName(user.id, tagName).associateByTo(HashMap()) { it.tagName }
    tagName.forEach {
        if (!existing.containsKey(it)) {
            existing[it] = save(Tag(user = user, tagName = it))
        }
    }
    return existing.values.toSet()
}