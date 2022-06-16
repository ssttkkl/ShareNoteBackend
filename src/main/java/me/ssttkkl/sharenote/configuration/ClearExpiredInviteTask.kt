package me.ssttkkl.sharenote.configuration

import me.ssttkkl.sharenote.repository.NoteInviteRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional


@Configuration
@EnableScheduling
class ClearExpiredInviteTask(val inviteRepo: NoteInviteRepository) {
    private val logger: Logger = LoggerFactory.getLogger(ClearExpiredInviteTask::class.java)

    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    fun run() {
        val result = inviteRepo.deleteExpired()
        logger.info("deleted $result expired note invites")
    }
}