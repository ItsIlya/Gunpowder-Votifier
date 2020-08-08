package io.github.gunpowder.votifier.entities

import io.github.gunpowder.votifier.GunpowderVotifierModule
import io.github.gunpowder.votifier.dataholders.Vote
import io.github.gunpowder.votifier.crypto.RSAIO
import io.github.gunpowder.votifier.crypto.RSAKeygen
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

        val port = 0
        receiver = VoteReceiver(this, host, port)
        receiver.start()
        logger.info("Votifier Started")
    }

    fun onVote(vote: Vote): Boolean {
        return true
    }

    fun shutdown() {
        logger.info("Shutting down Votifier")
        receiver.shutdown()
    }
}