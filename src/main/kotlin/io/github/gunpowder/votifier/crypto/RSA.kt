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
import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

class RSA {
    companion object {
        fun encrypt(data: ByteArray, key: PublicKey): ByteArray {
            return doFinal(data, key, Cipher.ENCRYPT_MODE)
        }

        fun decrypt(data: ByteArray, key: PrivateKey): ByteArray {
            return doFinal(data, key, Cipher.DECRYPT_MODE)
        }

        private fun doFinal(data: ByteArray, key: Key, mode: Int): ByteArray {
            val cipher: Cipher = Cipher.getInstance(GunpowderVotifierModule.instance.protocol)
            cipher.init(mode, key)
            return cipher.doFinal(data)
        }
    }
}