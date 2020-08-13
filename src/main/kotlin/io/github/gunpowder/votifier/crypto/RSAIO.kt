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

package io.github.gunpowder.votifier.crypto

import io.github.gunpowder.votifier.GunpowderVotifierModule
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.xml.bind.DatatypeConverter

class RSAIO {
    companion object {
        fun save(directory: File, pair: KeyPair) {
            val privateKey: PrivateKey = pair.private
            val publicKey: PublicKey = pair.public

            val publicSpec = X509EncodedKeySpec(publicKey.encoded)
            save("${directory}/public.key", publicSpec.encoded)

            val privateSpec = PKCS8EncodedKeySpec(privateKey.encoded)
            save("${directory}/public.key", privateSpec.encoded)
        }

        fun load(directory: File): KeyPair {
            val keyFactory: KeyFactory = KeyFactory.getInstance(GunpowderVotifierModule.instance.protocol)
            val encodedPublicKey = fileToByteArray(File("${directory}/public.key"))
            val encodedPrivateKey = fileToByteArray(File("${directory}/private.key"))

            val publicKeySpec = X509EncodedKeySpec(encodedPublicKey)
            val publicKey: PublicKey = keyFactory.generatePublic(publicKeySpec)

            val privateKeySpec = X509EncodedKeySpec(encodedPrivateKey)
            val privateKey: PrivateKey = keyFactory.generatePrivate(privateKeySpec)

            return KeyPair(publicKey, privateKey)
        }

        private fun save(file: String, data: ByteArray) {
            val out = FileOutputStream(file)
            out.write(DatatypeConverter.printBase64Binary(data).toByteArray())
            out.close()
        }

        private fun fileToByteArray(keyFile: File): ByteArray {
            val inputStream = FileInputStream(keyFile)
            var encodedKey = ByteArray(keyFile.length().toByte().toInt())
            inputStream.read(encodedKey)
            encodedKey = DatatypeConverter.parseBase64Binary(encodedKey.toString())
            inputStream.close()
            return encodedKey
        }
    }
}