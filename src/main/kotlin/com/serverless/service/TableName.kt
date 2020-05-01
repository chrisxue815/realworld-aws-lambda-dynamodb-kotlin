package com.serverless.service

val STAGE = System.getenv("STAGE") ?: "dev"

object TableName {
    val USER = makeTableName("user")
    val EMAIL_USER = makeTableName("email-user")
    val FOLLOW = makeTableName("follow")
    val ARTICLE = makeTableName("article")
    val ARTICLE_TAG = makeTableName("article-tag")
    val TAG = makeTableName("tag")
    val FAVORITE_ARTICLE = makeTableName("favorite-article")
    val COMMENT = makeTableName("comment")

    private fun makeTableName(suffix: String): String {
        return "realworld-kotlin-$STAGE-$suffix"
    }
}
