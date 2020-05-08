package com.serverless.service

import com.serverless.model.Article
import com.serverless.model.ArticleTag
import com.serverless.model.InputError
import com.serverless.model.MAX_ARTICLE_ID
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest

const val MAX_ATTEMPT = 5
const val MAX_QUERY_DEPTH = 1000

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
    val updates = mutableListOf<TransactWriteItem>() // DynamoDB enhanced client doesn't support update expression

    puts.put(Table.article) {
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
        puts.put(Table.articleTag) {
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

fun getArticles(
        offset: Int,
        limit: Int,
        author: String?,
        tag: String?,
        favorited: String?
): List<Article> {
    if (offset < 0) {
        throw InputError.build("offset", "must be non-negative")
    }

    if (limit <= 0) {
        throw InputError.build("limit", "must be positive")
    }

    if (offset + limit > MAX_QUERY_DEPTH) {
        throw InputError.build("offset + limit", "must be smaller or equal to $MAX_QUERY_DEPTH")
    }

    val numFilters = getNumFilters(author, tag, favorited)
    if (numFilters > 1) {
        throw InputError.build("author, tag, favorited", "only one of these can be specified")
    }

    if (numFilters == 0) {
        return getAllArticles(offset, limit)
    }

    if (!author.isNullOrBlank()) {
        return getArticlesByAuthor(author, offset, limit)
    }

    if (!tag.isNullOrBlank()) {
        return getArticlesByTag(tag, offset, limit)
    }

    if (!favorited.isNullOrBlank()) {
        return getFavoriteArticlesByUsername(favorited, offset, limit)
    }

    throw Exception("Unreachable code")
}

fun getNumFilters(
        author: String?,
        tag: String?,
        favorited: String?
): Int {
    var numFilters = 0
    if (!author.isNullOrBlank()) {
        numFilters++
    }
    if (!tag.isNullOrBlank()) {
        numFilters++
    }
    if (!favorited.isNullOrBlank()) {
        numFilters++
    }
    return numFilters
}

fun getAllArticles(
        offset: Int,
        limit: Int
): List<Article> {
    val query = QueryEnhancedRequest.builder().apply {
        queryConditional(keyEqualTo { it.partitionValue(0) })
        limit(offset + limit)
        scanIndexForward(false)
    }

    val pages = Table.articleByCreatedAt.query(query.build())

    return pages.flatMap { it.items() }.drop(offset).take(limit)
}

fun getArticlesByAuthor(
        author: String,
        offset: Int,
        limit: Int
): List<Article> {
    throw NotImplementedError()
}

fun getArticlesByTag(
        tag: String,
        offset: Int,
        limit: Int
): List<Article> {
    throw NotImplementedError()
}

fun getFavoriteArticlesByUsername(
        favorited: String,
        offset: Int,
        limit: Int
): List<Article> {
    throw NotImplementedError()
}
