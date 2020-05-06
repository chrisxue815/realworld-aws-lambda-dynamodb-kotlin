package com.serverless.service

import com.serverless.model.Article
import com.serverless.model.ArticleTag
import com.serverless.model.MAX_ARTICLE_ID
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest

const val MAX_ATTEMPT = 5

fun putArticle(article: Article) {
    article.validate()

    for (attempt in 0..MAX_ATTEMPT) {
        try {
            putArticleWithRandomId(article)
            return
        } catch (e: Throwable) {
            if (attempt >= MAX_ATTEMPT) {
                throw e
            }
            if (e !is ConditionalCheckFailedException) {
                throw e
            }
            articleIdRand.renewSeed()
        }
    }
}

fun putArticleWithRandomId(article: Article) {
    article.articleId = articleIdRand.get().nextLong(1, MAX_ARTICLE_ID)
    article.makeSlug()

    val puts = TransactWriteItemsEnhancedRequest.builder()
    val updates = mutableListOf<TransactWriteItem>() // Update expression is not yet supported by DynamoDB enhanced client

    puts.put(articleTable) {
        item(article)
        conditionExpression(attributeNotExists("articleId"))
    }

    for (tag in article.tagList) {
        val articleTag = ArticleTag(
                tag = tag,
                articleId = article.articleId,
                createdAt = article.createdAt
        )

        // Link article with tag
        puts.put(articleTagTable) {
            item(articleTag)
        }

        // Update article count for each tag
        val update = TransactWriteItem.builder().update {
            it.tableName(TableName.TAG)
            it.key("tag", tag)
            it.updateExpression("ADD articleCount :one SET dummy=:zero")
            it.expressionAttributeValues(mapOf(
                    ":one" to intValue(1),
                    ":zero" to intValue(0)
            ))
        }
        updates.add(update.build())
    }

    val transactItems = TransactWriteItemsRequest.builder()
            .transactItems(puts.build().transactWriteItems())
            .transactItems(updates)

    ddbClient.transactWriteItems(transactItems.build())
}
