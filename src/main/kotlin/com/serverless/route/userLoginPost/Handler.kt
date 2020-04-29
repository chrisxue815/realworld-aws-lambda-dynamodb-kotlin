package com.serverless.route.userLoginPost

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.model.InputError
import com.serverless.model.generateToken
import com.serverless.model.scrypt
import com.serverless.service.getUserByEmail
import com.serverless.util.JSON
import com.serverless.util.RealWorldRequestHandler
import com.serverless.util.ResponseBuilder
import kotlinx.serialization.Serializable

@Serializable
class Request(
        val user: User
) {
    @Serializable
    class User(
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

class Handler : RealWorldRequestHandler<Request>(Request.serializer()) {
    override fun handleRequest(request: Request, context: Context): APIGatewayV2ProxyResponseEvent {
        val user = getUserByEmail(request.user.email)
        val passwordHash = scrypt(request.user.password)

        if (!passwordHash.contentEquals(user.passwordHash)) {
            throw InputError.build("password", "wrong password")
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
