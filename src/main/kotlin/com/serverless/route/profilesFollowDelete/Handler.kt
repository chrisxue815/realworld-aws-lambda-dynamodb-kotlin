package com.serverless.route.profilesFollowDelete

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.service.getCurrentUser
import com.serverless.service.getUserByUsername
import com.serverless.service.unfollow
import com.serverless.util.JSON
import com.serverless.util.RealWorldRequestHandler
import com.serverless.util.ResponseBuilder
import com.serverless.util.UnauthorizedError
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
        if (user == null || token == null) {
            throw UnauthorizedError()
        }

        val publisher = getUserByUsername(input.pathParameters["username"])

        unfollow(user.username, publisher.username)

        val response = Response(
                profile = Response.Profile(
                        username = publisher.username,
                        image = publisher.image,
                        bio = publisher.bio,
                        following = false
                )
        )

        return ResponseBuilder.build {
            statusCode = 200
            rawBody = JSON.stringify(Response.serializer(), response)
        }
    }
}
