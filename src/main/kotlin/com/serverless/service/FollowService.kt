package com.serverless.service

import com.serverless.model.Follow
import com.serverless.model.User
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch

fun isFollowing(follower: User?, publishers: List<String>): BooleanArray {
    if (follower == null || publishers.isEmpty()) {
        return BooleanArray(publishers.size)
    }

    val readBatch = ReadBatch.builder(Follow::class.java).mappedTableResource(Table.follow)
    val keys = publishers.distinct().map { key(follower.username, it) }
    for (key in keys) {
        readBatch.addGetItem(key)
    }

    val pages = ddbEnhancedClient.batchGetItem {
        it.addReadBatch(readBatch.build())
    }

    val followingUsers = pages
            .flatMap { it.resultsForTable(Table.follow) }
            .map { it.publisher }
            .toSet()

    return publishers.map { followingUsers.contains(it) }.toBooleanArray()
}

fun follow(follower: String, publisher: String) {
    Table.follow.putItem(Follow(follower, publisher))
}

fun unfollow(follower: String, publisher: String) {
    Table.follow.deleteItem(key(follower, publisher))
}
