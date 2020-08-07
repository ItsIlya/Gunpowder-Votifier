package io.github.gunpowder.votifier.api

interface Vote {
    /**
     * The name of the Vote Service.
     */
    val serviceName: String

    /**
     * The username of the player who voted.
     */
    val userName: String

    /**
     * The address of player who voted.
     */
    val address: String

    /**
     * The data and time of when the vote was created.
     */
    val timeStamp: String
}