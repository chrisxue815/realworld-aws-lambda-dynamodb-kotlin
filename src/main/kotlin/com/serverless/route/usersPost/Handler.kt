package com.serverless.route.usersPost

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.model.User
import com.serverless.model.generateToken
import com.serverless.model.scrypt
import com.serverless.model.validatePassword
import com.serverless.service.putUser
import com.serverless.util.JSON
import com.serverless.util.RealWorldRequestHandler
import com.serverless.util.ResponseBuilder
import com.serverless.util.parseRequest
import kotlinx.serialization.Serializable

@Serializable
private class Request(
        val user: User
) {
    @Serializable
    class User(
            val username: String,
            val email: String,
            val password: String
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
        val request = JSON.parseRequest(Request.serializer(), input.body)

        validatePassword(request.user.password)

        val passwordHash = scrypt(request.user.password)

        val user = User(
                username = request.user.username,
                email = request.user.email,
                passwordHash = passwordHash
        )

        putUser(user)

        val token = generateToken(user.username)

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
