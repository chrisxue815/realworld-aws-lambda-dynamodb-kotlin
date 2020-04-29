package com.serverless.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

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
