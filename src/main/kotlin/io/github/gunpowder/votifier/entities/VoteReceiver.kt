package io.github.gunpowder.votifier.entities

import io.github.gunpowder.votifier.GunpowderVotifierModule
import io.github.gunpowder.votifier.dataholders.Vote
import io.github.gunpowder.votifier.crypto.RSA
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import javax.crypto.BadPaddingException

class VoteReceiver constructor(private val votifier: Votifier, host: String, port: Int) : Thread() {
    private val logger = GunpowderVotifierModule.gunpowder.logger
    private var running = true
    private val server: ServerSocket? = ServerSocket()

    init {
        try {
            server!!.bind(InetSocketAddress(host, port))
        } catch (e: Exception) {
            logger.error(
                    "Error initializing the vote receiver. " +
                            "Please verify that the configured IP Address and Port are not in use already.", e
            )
        }
    }

    fun shutdown() {
        running = false
        try {
            server!!.close()
        } catch (e: Exception) {
            logger.error("Unable to shutdown the vote receiver properly!")
        }
    }

    override fun run() {
        while (running) {
            try {
                val socket: Socket = server!!.accept()
                socket.soTimeout = 5000

                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                val input: InputStream = socket.getInputStream()

                writer.write("VOTIFIER ${votifier.version}")
                writer.newLine()
                writer.flush()

                var block = ByteArray(256)
                input.read(block, 0, block.size)

                block = RSA.decrypt(block, votifier.keyPair.private)
                var pos = 0

                val opCode = readFromData(block, pos)
                pos += opCode.length + 1
                if (opCode != "VOTE") {
                    throw Exception("Unable to decode RSA")
                }

                val serviceName = readFromData(block, pos)
                pos += serviceName.length + 1
                val userName = readFromData(block, pos)
                pos += userName.length + 1
                val address = readFromData(block, pos)
                pos += address.length + 1
                val timeStamp = readFromData(block, pos)

                votifier.onVote(Vote(serviceName, userName, address, timeStamp))

                writer.close()
                input.close()
                socket.close()
            } catch (e: SocketException) {
                logger.error("Protocol Error: Ignoring packet - ${e.localizedMessage}")
            } catch (e: BadPaddingException) {
                logger.error(
                        "Unable to decrypt the Vote Record," +
                                " Make sure that your public key matches the one you gave to the server list", e
                )
            } catch (e: Exception) {
                logger.error("Unexpected exception caught while receiving a Vote packet", e)
            }
        }
    }

    private fun readFromData(data: ByteArray, offset: Int): String {
        val builder: StringBuilder = StringBuilder()
        for (i in offset until data.size) {
            if (data[i].toChar() == '\n') break
            builder.append(data[i])
        }
        return builder.toString()
    }
}