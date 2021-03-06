package com.serverless.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

const val MIN_PASSWORD_LENGTH = 1
const val MIN_PASSWORD_LENGTH_ERROR = "must be at least $MIN_PASSWORD_LENGTH characters in length"
const val PASSWORD_HASH_LENGTH = 64

@DynamoDbBean
class User(
        @get:DynamoDbPartitionKey
        var username: String = "",
        var email: String = "",
        var passwordHash: ByteArray = ByteArray(0),
        var image: String? = null,
        var bio: String? = null
) {
    fun validate() {
        when {
            username == "" -> throw InputError.build("username", "can't be blank")
            email == "" -> throw InputError.build("email", "can't be blank")
            passwordHash.size != PASSWORD_HASH_LENGTH -> throw InputError.build("password", "invalid")
            //TODO: check max length, whitespace, etc
        }
    }
}

fun validatePassword(password: String) {
    when {
        password.length < MIN_PASSWORD_LENGTH -> throw InputError.build("password", MIN_PASSWORD_LENGTH_ERROR)
    }
}

@DynamoDbBean
class EmailUser(
        @get:DynamoDbPartitionKey
        var email: String = "",
        var username: String = ""
)

@DynamoDbBean
class Follow(
        @get:DynamoDbPartitionKey
        var follower: String = "",
        @get:DynamoDbSortKey
        var publisher: String = ""
)
