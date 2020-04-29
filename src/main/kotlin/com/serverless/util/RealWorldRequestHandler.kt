package com.serverless.util

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.model.InputError
import com.serverless.model.UnauthorizedError

interface RealWorldRequestHandler
    : RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    fun handleRequestSafely(input: APIGatewayV2ProxyRequestEvent, context: Context): APIGatewayV2ProxyResponseEvent

    override fun handleRequest(input: APIGatewayV2ProxyRequestEvent, context: Context): APIGatewayV2ProxyResponseEvent {
        return try {
            handleRequestSafely(input, context)
        } catch (e: InputError) {
            newInputErrorResponse(e)
        } catch (e: UnauthorizedError) {
            newUnauthorizedResponse()
        }
    }
}
