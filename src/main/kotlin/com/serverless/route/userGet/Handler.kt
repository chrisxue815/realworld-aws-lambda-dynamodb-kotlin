package com.serverless.route.userGet

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.service.getCurrentUser
import com.serverless.util.JSON
import com.serverless.util.RealWorldRequestHandler
import com.serverless.util.ResponseBuilder
import kotlinx.serialization.Serializable

@Serializable
private class Response(
        val user: User
) {
    @Serializable
    class User(
            val username: String,
            val email: String,
            val image: String?,
            val bio: String?,
            val token: String
    )
}

class Handler : RealWorldRequestHandler {
    override fun handleRequestSafely(input: APIGatewayV2ProxyRequestEvent, context: Context): APIGatewayV2ProxyResponseEvent {
        val (user, token) = getCurrentUser(input.headers["Authorization"])

        val response = Response(
                user = Response.User(
                        username = user.username,
                        email = user.email,
                        image = user.image,
                        bio = user.bio,
                        token = token
                )
        )

        return ResponseBuilder.build {
            statusCode = 201
            rawBody = JSON.stringify(Response.serializer(), response)
        }
    }
}
