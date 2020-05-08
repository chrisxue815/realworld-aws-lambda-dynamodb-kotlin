package com.serverless.service

import com.serverless.model.*
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

object Table {
    val user by lazy { ddbEnhancedClient.table(TableName.USER, TableSchema.fromBean(User::class.java))!! }
    val emailUser by lazy { ddbEnhancedClient.table(TableName.EMAIL_USER, TableSchema.fromBean(EmailUser::class.java))!! }
    val follow by lazy { ddbEnhancedClient.table(TableName.FOLLOW, TableSchema.fromBean(Follow::class.java))!! }
    val article by lazy { ddbEnhancedClient.table(TableName.ARTICLE, TableSchema.fromBean(Article::class.java))!! }
    val articleByCreatedAt by lazy { article.index("createdAt")!! }
    val articleByAuthor by lazy { article.index("author")!! }
    val articleTag by lazy { ddbEnhancedClient.table(TableName.ARTICLE_TAG, TableSchema.fromBean(ArticleTag::class.java))!! }
    val tag by lazy { ddbEnhancedClient.table(TableName.TAG, TableSchema.fromBean(Tag::class.java))!! }
}
