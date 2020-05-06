package com.serverless.route.articlesPost

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.model.Article
import com.serverless.service.getCurrentUser
import com.serverless.service.putArticle
import com.serverless.util.*
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneOffset

@Serializable
private class Request(
        val article: Article
) {
    @Serializable
    class Article(
            val title: String,
            val description: String,
            val body: String,
            val tagList: List<String>
    )
}

@Serializable
private class Response(
        val article: Article
) {
    @Serializable
    class Article(
            val slug: String,
            val title: String,
            val description: String,
            val body: String,
            val tagList: List<String>,
            val createdAt: String,
            val updatedAt: String,
            val favorited: Boolean,
            val favoritesCount: Long,
            val author: Author
    ) {
        @Serializable
        class Author(
                val username: String,
                val bio: String?,
                val image: String?,
                val following: Boolean
        )
    }
}

class Handler : RealWorldRequestHandler {
    override fun handleRequestSafely(input: APIGatewayV2ProxyRequestEvent, context: Context): APIGatewayV2ProxyResponseEvent {
        val (user, token) = getCurrentUser(input.headers["Authorization"])
        if (user == null || token == null) {
            throw UnauthorizedError()
        }

        val request = JSON.parseRequest(Request.serializer(), input.body)

        val now = Instant.now()
        val nowEpochMilli = now.toEpochMilli()
        val nowStr = Article.dateTimeFormatter.format(now.atOffset(ZoneOffset.UTC))

        val article = Article(
                title = request.article.title,
                description = request.article.description,
                body = request.article.body,
                tagList = request.article.tagList.distinct(),
                createdAt = nowEpochMilli,
                updatedAt = nowEpochMilli,
                author = user.username
        )

        putArticle(article)

        val response = Response(
                article = Response.Article(
                        slug = article.slug,
                        title = article.title,
                        description = article.description,
                        body = article.body,
                        tagList = article.tagList,
                        createdAt = nowStr,
                        updatedAt = nowStr,
                        favorited = false,
                        favoritesCount = 0,
                        author = Response.Article.Author(
                                username = user.username,
                                bio = user.bio,
                                image = user.image,
                                following = false
                        )
                )
        )

        return ResponseBuilder.build {
            statusCode = 201
            rawBody = JSON.stringify(Response.serializer(), response)
        }
    }
}
