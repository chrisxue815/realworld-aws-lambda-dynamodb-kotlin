package com.serverless.service

import com.serverless.model.InputError
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException

inline fun <reified T> TransactWriteItemsEnhancedRequest.Builder.put(
        mappedTableResource: MappedTableResource<T>,
        block: PutItemEnhancedRequest.Builder<T>.() -> Unit) {
    val builder = PutItemEnhancedRequest.builder(T::class.java)
    val putItem = builder.apply(block).build()
    addPutItem(mappedTableResource, putItem)
}

inline fun <reified T> TransactWriteItemsEnhancedRequest.Builder.delete(
        mappedTableResource: MappedTableResource<T>,
        block: DeleteItemEnhancedRequest.Builder.() -> Unit) {
    val builder = DeleteItemEnhancedRequest.builder()
    val deleteItem = builder.apply(block).build()
    addDeleteItem(mappedTableResource, deleteItem)
}

fun attributeNotExists(attribute: String): Expression {
    return Expression.builder().expression("attribute_not_exists($attribute)").build()
}

fun attributeExists(attribute: String): Expression {
    return Expression.builder().expression("attribute_exists($attribute)").build()
}

fun key(partitionValue: String): Key = Key.builder().partitionValue(partitionValue).build()

fun key(partitionValue: String, sortValue: String): Key = Key.builder().partitionValue(partitionValue).sortValue(sortValue).build()

fun throwInputError(e: TransactionCanceledException, block: () -> InputError) {
    if (e.cancellationReasons().any { it.code() == "ConditionalCheckFailed" }) {
        throw block()
    } else {
        throw e
    }
}
