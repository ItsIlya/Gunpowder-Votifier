package io.github.gunpowder.votifier.configs

data class VotifierConfig(
        val port: Int,
        val commands: List<String>
)