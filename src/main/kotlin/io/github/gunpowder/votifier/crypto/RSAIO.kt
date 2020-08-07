package io.github.gunpowder.votifier.crypto

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
    fun save(directory: File, pair: KeyPair) {
        val privateKey: PrivateKey = pair.private
        val publicKey: PublicKey = pair.public

        val publicSpec = X509EncodedKeySpec(publicKey.encoded)
        save("${directory}/public.key", publicSpec.encoded)

        val privateSpec = PKCS8EncodedKeySpec(privateKey.encoded)
        save("${directory}/public.key", privateSpec.encoded)
    }

    fun load(directory: File): KeyPair {
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
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