package io.github.gunpowder.votifier

import io.github.gunpowder.votifier.crypto.RSAIO
import io.github.gunpowder.votifier.crypto.RSAKeygen
import org.apache.logging.log4j.Logger
import java.io.File
import java.security.KeyPair

class Votifier {
    private val logger: Logger = GunpowderVotifierModule.gunpowder.logger
    private lateinit var receiver: VoteReceiver
    private lateinit var pair: KeyPair
    val version: String = "1.0"

    init {
        var host = GunpowderVotifierModule.gunpowder.server.serverIp
        if (host == null || host.isEmpty()) {
            host = "0.0.0.0"
        }

        val dir = File(System.getProperty("user.dir")).toPath().resolve("config").resolve("votifier").toFile()
        try {
            if (!dir.exists()) {
                dir.mkdirs()
                pair = RSAKeygen.generate(2048)
                RSAIO.save(dir, pair)
            } else {
                pair = RSAIO.load(dir)
            }

            val port = 0;
            receiver = VoteReceiver(host, port)
            receiver.start()

            logger.info("Starting up Votifier")
        } catch (e: Exception) {
            logger.error("Error while reading the RSA Key files", e)
            logger.error("Votifier did not initialize properly!")
        }
    }

    fun shutdown() {
        receiver.shutdown()
        logger.info("Votifier stopped.")
    }

    val keyPair: KeyPair
        get() = pair
}