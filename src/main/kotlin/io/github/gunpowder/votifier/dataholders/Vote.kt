package io.github.gunpowder.votifier.dataholders

data class Vote(
        /**
         * The name of the Vote Service.
         */
        val serviceName: String,

        /**
         * The username of the player who voted.
         */
        val userName: String,

        /**
         * The address of the player who voted.
         */
        val address: String,

        /**
         * The timestamp of the vote.
         */
        val timeStamp: String
)