package com.serverless.route.profilesGet

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.service.getCurrentUser
import com.serverless.service.getUserByUsername
import com.serverless.service.isFollowing
import com.serverless.util.JSON
import com.serverless.util.RealWorldRequestHandler
import com.serverless.util.ResponseBuilder
import kotlinx.serialization.Serializable

@Serializable
private class Response(
        val profile: Profile
) {
    @Serializable
    class Profile(
            val username: String,
            val image: String?,
            val bio: String?,
            val following: Boolean
    )
}

class Handler : RealWorldRequestHandler {
    override fun handleRequestSafely(input: APIGatewayV2ProxyRequestEvent, context: Context): APIGatewayV2ProxyResponseEvent {
        val (user, token) = getCurrentUser(input.headers["Authorization"])

        val publisher = getUserByUsername(input.pathParameters["username"])

        val following = isFollowing(user, listOf(publisher.username))

        val response = Response(
                profile = Response.Profile(
                        username = publisher.username,
                        image = publisher.image,
                        bio = publisher.bio,
                        following = following[0]
                )
        )

        return ResponseBuilder.build {
            statusCode = 200
            rawBody = JSON.stringify(Response.serializer(), response)
        }
    }
}
