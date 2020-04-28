package com.serverless.model

typealias InputError = Map<String, List<String>>

fun newInputError(field: String, error: String) = mapOf(field to listOf(error))
