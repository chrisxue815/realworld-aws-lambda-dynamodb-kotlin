package com.serverless.util

import com.serverless.model.InputError
import kotlinx.serialization.Serializable

@Serializable
class InputErrorResponse(
        val errors: Map<String, List<String>>
)

fun newInputErrorResponse(err: InputError) = newInputErrorResponse(err.errors)

fun newInputErrorResponse(errors: Map<String, List<String>>) = ResponseBuilder.build {
    statusCode = 422
    rawBody = JSON.stringify(InputErrorResponse.serializer(), InputErrorResponse(errors))
}

fun newUnauthorizedResponse() = ResponseBuilder.build {
    statusCode = 401
}
