package com.serverless.service

import com.serverless.model.Article
import com.serverless.model.User

fun isArticleFavoritedByUser(user: User?, articles: List<Article>): BooleanArray {
    if (user == null || articles.isEmpty()) {
        return BooleanArray(articles.size)
    }

    throw NotImplementedError()
}
