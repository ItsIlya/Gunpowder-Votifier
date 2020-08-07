package io.github.gunpowder.votifier.crypto

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

class RSA {
    fun encrypt(data: ByteArray, key: PublicKey): ByteArray {
        return doFinal(data, key, Cipher.ENCRYPT_MODE)
    }

    fun decrypt(data: ByteArray, key: PrivateKey): ByteArray {
        return doFinal(data, key, Cipher.DECRYPT_MODE)
    }

    private fun doFinal(data: ByteArray, key: Key, mode: Int): ByteArray {
        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(mode, key)
        return cipher.doFinal(data)
    }
}