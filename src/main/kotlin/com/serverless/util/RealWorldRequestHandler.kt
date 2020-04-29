package com.serverless.util

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent
import com.serverless.model.InputError
import kotlinx.serialization.DeserializationStrategy

abstract class RealWorldRequestHandler<R>(
        private val requestDeserializer: DeserializationStrategy<R>
) : RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    abstract fun handleRequest(request: R, context: Context): APIGatewayV2ProxyResponseEvent

    override fun handleRequest(input: APIGatewayV2ProxyRequestEvent, context: Context): APIGatewayV2ProxyResponseEvent {
        try {
            val request = JSON.parse(requestDeserializer, input.body)
            return handleRequest(request, context)
        } catch (err: InputError) {
            return newInputErrorResponse(err)
        }
    }
}
