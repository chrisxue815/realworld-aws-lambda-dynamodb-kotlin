package com.serverless.service

import com.serverless.model.EmailUser
import com.serverless.model.Follow
import com.serverless.model.User
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

val ddbClient by lazy { DynamoDbEnhancedClient.create()!! }

val userTable by lazy { ddbClient.table(TableName.USER, TableSchema.fromBean(User::class.java))!! }
val emailUserTable by lazy { ddbClient.table(TableName.EMAIL_USER, TableSchema.fromBean(EmailUser::class.java))!! }
val followTable by lazy { ddbClient.table(TableName.FOLLOW, TableSchema.fromBean(Follow::class.java))!! }
