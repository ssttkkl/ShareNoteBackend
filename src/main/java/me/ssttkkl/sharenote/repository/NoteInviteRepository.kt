package me.ssttkkl.sharenote.repository;

import me.ssttkkl.sharenote.model.entity.NoteInvite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface NoteInviteRepository : JpaRepository<NoteInvite, String> {
    @Modifying
    @Query(
        """
        delete 
        from NoteInvite ni
        where ni.expiresAt < ?1
    """
    )
    fun deleteExpired(currentTime: Instant = Instant.now()): Int
}