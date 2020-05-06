package com.serverless.model

import com.github.slugify.Slugify
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.time.format.DateTimeFormatter

const val MAX_ARTICLE_ID = 0x1000000L // exclusive
const val MAX_NUM_TAGS_PER_ARTICLE = 5
const val MAX_NUM_TAGS_PER_ARTICLE_EXCEEDED = "cannot add more than $MAX_NUM_TAGS_PER_ARTICLE tags per article"

@DynamoDbBean
class Article(
        @get:DynamoDbPartitionKey
        var articleId: Long = 0,
        var slug: String = "",
        var title: String = "",
        var description: String = "",
        var body: String = "",
        var tagList: List<String> = emptyList(),
        @get:DynamoDbSecondarySortKey(indexNames = ["createdAt", "author"])
        var createdAt: Long = 0,
        var updatedAt: Long = 0,
        var favoritesCount: Long = 0,
        @get:DynamoDbSecondaryPartitionKey(indexNames = ["author"])
        var author: String = "",
        @get:DynamoDbSecondaryPartitionKey(indexNames = ["createdAt"])
        var dummy: Byte = 0 // Always 0, used for sorting articles by index createdAt
) {
    fun validate() {
        if (tagList.size > MAX_NUM_TAGS_PER_ARTICLE) {
            throw InputError.build("tagList", MAX_NUM_TAGS_PER_ARTICLE_EXCEEDED)
        }
    }

    fun makeSlug() {
        val slugPrefix = Slugify().slugify(title)
        val articleIdHex = articleId.toString(16)
        slug = "$slugPrefix-$articleIdHex"
    }

    companion object {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX")!!

        fun slugToArticleId(slug: String): Long {
            val articleIdStr = slug.substringAfterLast('-')
            try {
                return articleIdStr.toLong(16)
            } catch (e: NumberFormatException) {
                throw InputError.build("slug", "invalid")
            }
        }
    }
}

@DynamoDbBean
class ArticleTag(
        @get:DynamoDbPartitionKey
        @get:DynamoDbSecondaryPartitionKey(indexNames = ["createdAt"])
        var tag: String = "",
        @get:DynamoDbSortKey
        var articleId: Long = 0,
        @get:DynamoDbSecondarySortKey(indexNames = ["createdAt"])
        var createdAt: Long = 0
)

@DynamoDbBean
class Tag(
        @get:DynamoDbPartitionKey
        var tag: String = "",
        @get:DynamoDbSecondarySortKey(indexNames = ["articleCount"])
        var articleCount: Long = 0,
        @get:DynamoDbSecondaryPartitionKey(indexNames = ["articleCount"])
        var dummy: Byte = 0 // Always 0, used for sorting articles by index articleCount
)
