package com.serverless.service

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

val ddbClient by lazy { DynamoDbClient.create()!! }
val ddbEnhancedClient by lazy { DynamoDbEnhancedClient.builder().dynamoDbClient(ddbClient).build()!! }
