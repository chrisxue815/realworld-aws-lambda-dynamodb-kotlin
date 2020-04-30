package com.serverless.route.userPut

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.model.User
import com.serverless.model.scrypt
import com.serverless.model.validatePassword
import com.serverless.service.getCurrentUser
import com.serverless.service.updateUser
import com.serverless.util.*
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable

@Serializable
private class Request(
        val user: User
) {
    @Serializable
    class User(
            val email: String,
            val password: String,
            val image: String? = null,
            val bio: String? = null
    )
}

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
        val (oldUser, token) = getCurrentUser(input.headers["Authorization"])
        if (oldUser == null || token == null) {
            throw UnauthorizedError()
        }

        val request = JSON.parseRequest(Request.serializer(), input.body)

        validatePassword(request.user.password)

        val passwordHash = scrypt(request.user.password)

        val newUser = User(
                username = oldUser.username,
                email = request.user.email,
                passwordHash = passwordHash,
                image = request.user.image,
                bio = request.user.bio
        )

        updateUser(oldUser, newUser)

        val response = Response(
                user = Response.User(
                        username = newUser.username,
                        email = newUser.email,
                        image = newUser.image,
                        bio = newUser.bio,
                        token = token
                )
        )

        return ResponseBuilder.build {
            statusCode = 201
            rawBody = JSON.stringify(Response.serializer(), response)
        }
    }
}
