package com.serverless.service

import com.serverless.model.Follow
import software.amazon.awssdk.enhanced.dynamodb.TableMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

class DynamoDBClientTest {
    @Test
    fun `Table follow should have partition key named follower`() {
        val actual = Table.follow.tableSchema().tableMetadata().indexPartitionKey(TableMetadata.primaryIndexName())
        val expected = Follow::follower.name
        assertEquals(expected, actual)
    }
}
