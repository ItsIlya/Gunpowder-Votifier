package io.github.gunpowder.votifier.crypto

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.RSAKeyGenParameterSpec

class RSAKeygen {
    companion object {
        fun generate(bits: Int): KeyPair {
            val gen: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
            val spec = RSAKeyGenParameterSpec(bits, RSAKeyGenParameterSpec.F4)
            gen.initialize(spec)
            return gen.generateKeyPair()
        }
    }
}