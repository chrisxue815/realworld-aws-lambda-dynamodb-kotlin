package com.serverless.route.articlesGet

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.model.Article
import com.serverless.service.getArticles
import com.serverless.service.getCurrentUser
import com.serverless.util.JSON
import com.serverless.util.RealWorldRequestHandler
import com.serverless.util.ResponseBuilder
import kotlinx.serialization.Serializable

@Serializable
private class Response(
        val articles: List<Article>,
        val articlesCount: Int
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

        val offset = input.queryStringParameters?.get("offset")?.toInt() ?: 0
        val limit = input.queryStringParameters?.get("limit")?.toInt() ?: 20
        val author = input.queryStringParameters?.get("author")
        val tag = input.queryStringParameters?.get("tag")
        val favorited = input.queryStringParameters?.get("favorited")

        val articles = getArticles(offset, limit, author, tag, favorited)

        val response = Response(
                articles = articles.map { article ->
                    Response.Article(
                            slug = article.slug,
                            title = article.title,
                            description = article.description,
                            body = article.body,
                            tagList = article.tagList,
                            createdAt = Article.epochMilliToStr(article.createdAt),
                            updatedAt = Article.epochMilliToStr(article.updatedAt),
                            favorited = false,
                            favoritesCount = 0,
                            author = Response.Article.Author(
                                    username = "",
                                    bio = null,
                                    image = null,
                                    following = false
                            )
                    )
                },
                articlesCount = articles.size
        )

        return ResponseBuilder.build {
            statusCode = 200
            rawBody = JSON.stringify(Response.serializer(), response)
        }
    }
}
