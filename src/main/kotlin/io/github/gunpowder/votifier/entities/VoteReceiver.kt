/*
 * MIT License
 *
 * Copyright (c) 2020 GunpowderMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

                val opCode = readNextString(block, pos)
                pos += opCode.length + 1
                if (opCode != "VOTE") {
                    throw Exception("Unable to decode RSA")
                }

                val serviceName = readNextString(block, pos)
                pos += serviceName.length + 1
                val userName = readNextString(block, pos)
                pos += userName.length + 1
                val address = readNextString(block, pos)
                pos += address.length + 1
                val timeStamp = readNextString(block, pos)

                GunpowderVotifierModule.gunpowder.server.execute {
                    votifier.onVote(Vote(serviceName, userName, address, timeStamp))
                }

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

}
private fun readNextString(data: ByteArray, offset: Int): String {
    val builder: StringBuilder = StringBuilder()
    for (i in offset until data.size) {
        if (data[i].toChar() == '\n') break
        builder.append(data[i])
    }
    return builder.toString()
}
