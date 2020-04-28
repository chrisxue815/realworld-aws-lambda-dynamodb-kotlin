package com.serverless.util

import com.serverless.model.InputError
import kotlinx.serialization.Serializable

@Serializable
class InputErrorResponse(
        val errors: InputError
)

fun newInputErrorResponse(err: InputError) = ResponseBuilder.build {
    statusCode = 422
    rawBody = JSON.stringify(InputErrorResponse.serializer(), InputErrorResponse(err))
}
