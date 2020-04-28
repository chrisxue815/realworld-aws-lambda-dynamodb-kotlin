package com.serverless.route.userPost

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.model.User
import com.serverless.model.generateToken
import com.serverless.model.scrypt
import com.serverless.model.validatePassword
import com.serverless.service.putUser
import com.serverless.util.JSON
import com.serverless.util.ResponseBuilder
import com.serverless.util.newInputErrorResponse
import kotlinx.serialization.Serializable

@Serializable
class Request(
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
class Response(
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

class Handler : RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {
    override fun handleRequest(input: APIGatewayV2ProxyRequestEvent, context: Context): APIGatewayV2ProxyResponseEvent {
        val request = JSON.parse(Request.serializer(), input.body)

        var err = validatePassword(request.user.password)
        if (err != null) {
            return newInputErrorResponse(err)
        }

        val passwordHash = scrypt(request.user.password)

        val user = User(
                username = request.user.username,
                email = request.user.email,
                passwordHash = passwordHash
        )

        err = putUser(user)
        if (err != null) {
            return newInputErrorResponse(err)
        }

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
