package com.serverless.service

import com.serverless.model.*
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

val ddbClient by lazy { DynamoDbClient.create()!! }
val ddbEnhancedClient by lazy { DynamoDbEnhancedClient.builder().dynamoDbClient(ddbClient).build()!! }

val userTable by lazy { ddbEnhancedClient.table(TableName.USER, TableSchema.fromBean(User::class.java))!! }
val emailUserTable by lazy { ddbEnhancedClient.table(TableName.EMAIL_USER, TableSchema.fromBean(EmailUser::class.java))!! }
val followTable by lazy { ddbEnhancedClient.table(TableName.FOLLOW, TableSchema.fromBean(Follow::class.java))!! }
val articleTable by lazy { ddbEnhancedClient.table(TableName.ARTICLE, TableSchema.fromBean(Article::class.java))!! }
val articleTagTable by lazy { ddbEnhancedClient.table(TableName.ARTICLE_TAG, TableSchema.fromBean(ArticleTag::class.java))!! }
val tagTable by lazy { ddbEnhancedClient.table(TableName.TAG, TableSchema.fromBean(Tag::class.java))!! }
