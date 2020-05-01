package com.serverless.service

import com.serverless.model.Follow
import com.serverless.model.User
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch

fun isFollowing(follower: User?, publishers: List<String>): BooleanArray {
    if (follower == null || publishers.isEmpty()) {
        return BooleanArray(0)
    }

    val readBatch = ReadBatch.builder(Follow::class.java).mappedTableResource(followTable)
    val keys = publishers.distinct().map { key(follower.username, it) }
    for (key in keys) {
        readBatch.addGetItem(key)
    }

    val pages = ddbClient.batchGetItem {
        it.addReadBatch(readBatch.build())
    }

    val followingUsers = pages
            .flatMap { it.resultsForTable(followTable) }
            .map { it.publisher }
            .toSet()

    return publishers.map { followingUsers.contains(it) }.toBooleanArray()
}
