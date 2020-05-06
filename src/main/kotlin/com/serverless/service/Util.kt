package com.serverless.service

import com.serverless.model.InputError
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException
import software.amazon.awssdk.services.dynamodb.model.Update

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

inline fun <reified T> TransactWriteItemsEnhancedRequest.Builder.update(
        mappedTableResource: MappedTableResource<T>,
        block: UpdateItemEnhancedRequest.Builder<T>.() -> Unit) {
    val builder = UpdateItemEnhancedRequest.builder(T::class.java)
    val updateItem = builder.apply(block).build()
    addUpdateItem(mappedTableResource, updateItem)
}

fun attributeNotExists(attribute: String): Expression {
    return Expression.builder()
            .expression("attribute_not_exists($attribute)")
            .build()
}

fun attributeExists(attribute: String): Expression {
    return Expression.builder()
            .expression("attribute_exists($attribute)")
            .build()
}

fun attributeEquals(attribute: String, value: AttributeValue): Expression {
    return Expression.builder()
            .expression("$attribute = :value")
            .putExpressionValue(":value", value)
            .build()
}

fun attributeEquals(attribute: String, value: String) = attributeEquals(attribute, stringValue(value))

fun stringValue(value: String): AttributeValue = AttributeValue.builder().s(value).build()

fun intValue(value: Int): AttributeValue = AttributeValue.builder().n(value.toString()).build()

fun key(partitionValue: String): Key = Key.builder().partitionValue(partitionValue).build()

fun key(partitionValue: String, sortValue: String): Key = Key.builder().partitionValue(partitionValue).sortValue(sortValue).build()

fun Update.Builder.key(partitionKey: String, partitionValue: String): Update.Builder = key(mapOf(partitionKey to stringValue(partitionValue)))

fun throwInputError(e: TransactionCanceledException, block: () -> InputError) {
    if (e.cancellationReasons().any { it.code() == "ConditionalCheckFailed" }) {
        throw block()
    } else {
        throw e
    }
}
