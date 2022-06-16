package me.ssttkkl.sharenote.service

import me.ssttkkl.sharenote.exception.NoteForbiddenException
import me.ssttkkl.sharenote.exception.NoteNotFoundException
import me.ssttkkl.sharenote.exception.NoteVersionConflictException
import me.ssttkkl.sharenote.model.dto.NoteDto
import me.ssttkkl.sharenote.model.entity.*
import me.ssttkkl.sharenote.model.view.NoteView
import me.ssttkkl.sharenote.model.view.PageView
import me.ssttkkl.sharenote.model.view.toView
import me.ssttkkl.sharenote.repository.*
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Service
class NoteService(
    val noteRepo: NoteRepository,
    val userRepo: UserRepository,
    val tagRepo: TagRepository,
    val versionRepo: NoteVersionRepository,
) {

    @Transactional
    fun getUserNotes(
        titleKeywords: Set<String>,
        tags: Set<Int>,
        readonly: Boolean,
        userID: Int,
        pageable: Pageable
    ): PageView<NoteView> {
        val spec = NoteSpecBuilder()
            .setUserID(userID)
            .also {
                if (titleKeywords.isNotEmpty())
                    it.setTitleKeyword(titleKeywords)
            }
            .also {
                if (tags.isNotEmpty())
                    it.setTags(tags, userID)
            }.setReadonly(readonly)
            .build()

        return noteRepo.findAll(spec, pageable).map { it.toView(userID) }.toView()
    }

    @Transactional
    fun getNoteByID(
        noteID: Int,
        userID: Int,
        version: Int = 0,
    ): NoteView {
        val note = noteRepo.findById(noteID).orElseThrow { NoteNotFoundException() }
        // note.toView will check permission
        return if (version != 0) {
            val version = versionRepo.findByNoteIdAndVersion(noteID, version)
            note.toView(userID, version)
        } else {
            note.toView(userID)
        }
    }

    fun getNoteAllVersionsByID(
        noteID: Int,
        userID: Int,
        pageable: Pageable
    ): PageView<NoteView> {
        val note = noteRepo.findById(noteID).orElseThrow { NoteNotFoundException() }
        // note.toView will check permission
        val versions = versionRepo.findByNoteId(noteID, pageable)
        return versions.map { note.toView(userID, it) }.toView()
    }

    @Transactional
    fun createNote(
        dto: NoteDto,
        userID: Int
    ): NoteView {
        val user = userRepo.getReferenceById(userID)

        var note = Note(ownerUser = user)
        note = noteRepo.save(note)

        note.permissions.add(NotePermission(note = note, user = user))

        val version = NoteVersion(
            note = note,
            creator = user,
            title = dto.title,
            content = dto.content
        )
        note.versions.add(version)
        note.latestVersion = version

        note = noteRepo.save(note)

        return note.toView(userID)
    }

    fun modifyNote(
        noteID: Int,
        dto: NoteDto,
        userID: Int,
        ignoreConflict: Boolean,
    ): NoteView {
        val user = userRepo.getReferenceById(userID)
        var note = noteRepo.findById(noteID).orElseThrow { NoteNotFoundException() }

        // check permission
        val permission = note.permissions.find { it.user.id == userID }
        if (permission == null || permission.isDeleted || permission.readonly) {
            throw NoteForbiddenException()
        }

        // check version
        val prevVersion = note.latestVersion
        if (!ignoreConflict && prevVersion?.version != dto.version) {
            throw NoteVersionConflictException()
        }

        val version = NoteVersion(
            id = 0,
            note = note,
            version = prevVersion?.version?.plus(1) ?: 1,
            creator = user,
            title = dto.title,
            content = dto.content
        )
        note.latestVersion = version
        note.versions.add(version)

        note = noteRepo.save(note)
        return note.toView(userID)
    }

    @Transactional
    fun deleteNote(
        noteID: Int,
        userID: Int
    ) {
        val note = noteRepo.findById(noteID).orElseThrow { NoteNotFoundException() }
        if (!note.isDeleted && note.ownerUser.id == userID) {
            val now = Instant.now()
            note.deletedAt = now
            note.permissions.forEach {
                if (it.user.id == userID)
                    it.state = NotePermission.State.DeletedBySelf
                else
                    it.state = NotePermission.State.DeletedByOwner
                it.deletedAt = now
                it.deletedAtVersion = note.latestVersion
            }
            note.tags.removeIf { it.user.id == userID }
            noteRepo.save(note)
        } else {
            throw NoteForbiddenException()
        }
    }

    @Transactional
    fun setNoteTags(noteID: Int, tagName: Set<String>, userID: Int) {
        val user = userRepo.getReferenceById(userID)
        val note = noteRepo.findById(noteID).orElseThrow { NoteNotFoundException() }

        // check permission
        val permission = note.permissions.find { it.user.id == userID }
        if (permission == null || permission.isDeleted) {
            throw NoteForbiddenException()
        }

        val tags = tagRepo.findOrInsertByUserAndTagName(user, tagName)
        note.tags.removeIf { it.user.id == userID }
        note.tags.addAll(tags)
        noteRepo.save(note)
    }

    private class NoteSpecBuilder {
        private var _spec: Specification<Note>? = null

        private fun compose(nextSpec: Specification<Note>) {
            _spec = _spec?.and(nextSpec) ?: nextSpec
        }

        fun build(): Specification<Note> = Specification.where(_spec)

        fun setUserID(userID: Int): NoteSpecBuilder {
            /*
            select n
            from Note n
            join n.permissions np
            where np.user.id = :userID
            and np.state != State.DeletedBySelf
             */
            compose { root, query, cb ->
                val join = root.join<Note, NotePermission>("permissions")
                cb.and(
                    cb.equal(join.get<User>("user").get<Int>("id"), userID),
                    cb.notEqual(join.get<NotePermission.State>("state"), NotePermission.State.DeletedBySelf)
                )
            }
            return this
        }

        fun setTitleKeyword(keywords: Set<String>): NoteSpecBuilder {
            keywords.forEach { word ->
                // note.latestVersion.title like ("%" + :word + "%")
                compose { root, query, cb ->
                    cb.like(root.get<NoteVersion>("latestVersion").get("title"), "%${word}%")
                }
            }
            return this
        }

        fun setTags(tagID: Set<Int>, userID: Int): NoteSpecBuilder {
            compose { root, query, cb ->
                /*
                not exists (
                    select t
                    from note.tags t
                    where t.user_id = :userID
                      and t.id not in :tagID
                )
                */
                val subQuery = query.subquery(Tag::class.java)
                val join = subQuery.correlate(root).join<Note, Tag>("tags")
                subQuery.select(join)
                    .where(
                        cb.and(
                            cb.equal(join.get<User>("user").get<Int>("id"), userID),
                            cb.not(cb.`in`(join.get<Int>("id")).apply {
                                tagID.forEach { value(it) }
                            })
                        )
                    )

                cb.not(cb.exists(subQuery))
            }
            return this
        }

        fun setReadonly(readonly: Boolean): NoteSpecBuilder {
            /*
            select n
            from Note n
            join n.permissions np
            where np.readonly = :readonly
             */
            if (readonly) {
                compose { root, query, cb ->
                    cb.isTrue(
                        root.join<Note, NotePermission>("permissions")
                            .get("readonly")
                    )
                }
            }
            return this
        }
    }
}