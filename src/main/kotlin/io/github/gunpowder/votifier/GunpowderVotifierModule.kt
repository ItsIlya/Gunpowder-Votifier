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

package io.github.gunpowder.votifier

import io.github.gunpowder.api.GunpowderMod
import io.github.gunpowder.api.GunpowderModule
import io.github.gunpowder.votifier.commands.VotifierCommand
import io.github.gunpowder.votifier.configs.VotifierConfig
import io.github.gunpowder.votifier.entities.Votifier
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.lang.Exception

class GunpowderVotifierModule : GunpowderModule {
    override val name = "votifier"
    override val toggleable = true

    companion object {
        val logger: Logger = LogManager.getLogger("Votifier")
        val directory: File = File(System.getProperty("user.dir")).toPath().resolve("config").resolve("votifier").toFile()
        private var votifier: Votifier? = null
        val config: VotifierConfig = gunpowder.registry.getConfig(VotifierConfig::class.java)

        val gunpowder: GunpowderMod
            get() = GunpowderMod.instance

        @JvmStatic
        val instance: Votifier
            get() = votifier ?: throw IllegalStateException("Votifier instance hasn't been initialized!")
    }

    override fun registerConfigs() {
        gunpowder.registry.registerConfig("gunpowder-votifier.yaml", VotifierConfig::class.java, "gunpowder-votifier.yaml")
    }

    override fun registerCommands() {
        gunpowder.registry.registerCommand(VotifierCommand::register)
    }

    override fun onInitialize() {
        if (!directory.exists()) {
            directory.mkdirs()
        }

        votifier = try {
            Votifier
        } catch (e: Exception) {
            logger.error("Votifier did not initialize properly!", e)
            null
        }

        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping {
            votifier!!.shutdown()
        })
    }

}
