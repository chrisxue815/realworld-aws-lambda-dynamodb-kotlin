package com.serverless.service

import java.time.Instant
import kotlin.random.Random

class Rand(
        private var random: Random = Random.Default
) {
    fun renewSeed() {
        random = Random(Instant.now().toEpochMilli())
    }

    fun get() = random
}

var articleIdRand = Rand()
