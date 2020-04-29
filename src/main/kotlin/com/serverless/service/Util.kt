package com.serverless.service

import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest

inline fun <reified T> TransactWriteItemsEnhancedRequest.Builder.put(
        mappedTableResource: MappedTableResource<T>,
        block: PutItemEnhancedRequest.Builder<T>.() -> Unit) {
    val builder = PutItemEnhancedRequest.builder(T::class.java)
    val putItem = builder.apply(block).build()
    addPutItem(mappedTableResource, putItem)
}

fun <T> PutItemEnhancedRequest.Builder<T>.ifAttributeNotExists(attribute: String): PutItemEnhancedRequest.Builder<T> {
    return conditionExpression(attributeNotExists(attribute))
}

fun attributeNotExists(attribute: String): Expression {
    return Expression.builder().expression("attribute_not_exists($attribute)").build()
}

fun key(partitionValue: String): Key = Key.builder().partitionValue(partitionValue).build()

fun key(partitionValue: String, sortValue: String): Key = Key.builder().partitionValue(partitionValue).sortValue(sortValue).build()
