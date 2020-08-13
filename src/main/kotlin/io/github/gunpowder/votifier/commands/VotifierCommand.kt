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

package io.github.gunpowder.votifier.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import io.github.gunpowder.api.builders.Command
import io.github.gunpowder.votifier.GunpowderVotifierModule
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource

object VotifierCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        Command.builder(dispatcher) {
            command("votes") {
                literal("add") {
                    argument("target", EntityArgumentType.entities()) {
                        argument("amount", IntegerArgumentType.integer(1)) {

                        }
                    }
                }
                literal("set") {

                }
                literal("remove") {

                }
                literal("query") {
                    argument("target", EntityArgumentType.entity()) {
                        executes(::query)
                        argument("trigger", BoolArgumentType.bool()) {
                            executes(::query)
                        }
                    }
                }
            }
        }

    }

    private fun query(ctx: CommandContext<ServerCommandSource>): Int {
        GunpowderVotifierModule.directory

        return 1
    }
}