package com.serverless.util

import com.serverless.model.InputError
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

// TODO: Is Json thread safe?
val JSON = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))

fun <T> Json.parseRequest(deserializer: DeserializationStrategy<T>, string: String): T {
    try {
        return parse(deserializer, string)
    } catch (e: MissingFieldException) {
        throw e.toInputError()
    }
}

private val missingFieldExceptionMessagePattern = Regex("Field '(?<fieldName>.*)' is required, but it was missing")

private fun MissingFieldException.toInputError(): InputError {
    val match = missingFieldExceptionMessagePattern.matchEntire(message!!)
    val fieldName = match!!.groups["fieldName"]!!.value
    return InputError.build(fieldName, "missing")
}
