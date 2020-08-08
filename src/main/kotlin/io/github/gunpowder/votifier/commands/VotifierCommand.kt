package io.github.gunpowder.votifier.commands

import com.mojang.brigadier.CommandDispatcher
import io.github.gunpowder.api.builders.Command
import net.minecraft.server.command.ServerCommandSource

object VotifierCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        Command.builder(dispatcher) {
            command("votifier") {
            }
        }
    }
}