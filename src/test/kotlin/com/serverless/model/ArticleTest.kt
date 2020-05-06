package com.serverless.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class ArticleTest {
    @Test
    fun `dateTimeFormatter should work`() {
        val timestamp = OffsetDateTime.of(2020, 1, 2, 3, 4, 5, 600_000_000, ZoneOffset.UTC)
        val expected = "2020-01-02T03:04:05.600Z"
        val actual = Article.dateTimeFormatter.format(timestamp)
        assertEquals(expected, actual)
    }
}
