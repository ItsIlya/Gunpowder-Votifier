package io.github.gunpowder.votifier.entities

import io.github.gunpowder.votifier.GunpowderVotifierModule
import io.github.gunpowder.votifier.crypto.RSAIO
import io.github.gunpowder.votifier.crypto.RSAKeygen
import io.github.gunpowder.votifier.dataholders.Vote
import org.apache.logging.log4j.Logger
import java.io.File
import java.security.KeyPair

object Votifier {
    private val logger: Logger = GunpowderVotifierModule.logger
    const val version = "1.0"
    const val protocol = "RSA"
    val keyPair: KeyPair
    private val receiver: VoteReceiver

    init {
        var host = GunpowderVotifierModule.gunpowder.server.serverIp
        if (host == null || host.isEmpty()) {
            host = "0.0.0.0"
        }

        val keysDir = File("${GunpowderVotifierModule.directory}/keys")
        if (!keysDir.exists()) {
            keysDir.mkdirs()
            keyPair = RSAKeygen.generate(2048)
            RSAIO.save(keysDir, keyPair)
        } else {
            keyPair = RSAIO.load(keysDir)
        }

        receiver = VoteReceiver(this, host, GunpowderVotifierModule.config.port)
        receiver.start()
        logger.info("Votifier Started")
    }

    fun onVote(vote: Vote): Boolean {
        val server = GunpowderVotifierModule.gunpowder.server
        server.execute {
            val profile = server.userCache.findByName(vote.userName)

            for (command in GunpowderVotifierModule.config.commands) {
                server.commandManager.execute(
                        server.commandSource,
                        command.replace("%SERVICE%", vote.serviceName)
                                .replace("%PLAYER%", profile?.name ?: vote.userName)
                                .replace("%TIMESTAMP%", vote.timeStamp)
                                .replace("%ADDRESS%", vote.address)
                )
            }
        }

        return true
    }

    fun shutdown() {
        logger.info("Shutting down Votifier")
        receiver.shutdown()
    }
}