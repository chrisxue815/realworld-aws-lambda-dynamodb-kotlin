package com.serverless.service

import com.serverless.model.EmailUser
import com.serverless.model.InputError
import com.serverless.model.User
import com.serverless.model.verifyAuthorization
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException

fun putUser(user: User): InputError? {
    user.validate()

    val emailUser = EmailUser(
            email = user.email,
            username = user.username
    )

    try {
        ddbClient.transactWriteItems {
            it.put(userTable) {
                item(user)
                ifAttributeNotExists("username")
            }
            it.put(emailUserTable) {
                item(emailUser)
                ifAttributeNotExists("email")
            }
        }
    } catch (e: TransactionCanceledException) {
        if (e.cancellationReasons().any { it.code() == "ConditionalCheckFailed" }) {
            return InputError.build(
                    "username" to listOf("has already been taken"),
                    "email" to listOf("has already been taken")
            )
        } else {
            throw e
        }
    }

    return null
}

fun getUserByEmail(email: String): User {
    if (email.isEmpty()) {
        throw InputError.build("email", "can't be blank")
    }

    val username = getUsernameByEmail(email)

    return getUserByUsername(username)
}

fun getUsernameByEmail(email: String): String {
    val emailUser = emailUserTable.getItem(key(email))
            ?: throw InputError.build("email", "not found")

    return emailUser.username
}

fun getUserByUsername(username: String): User {
    if (username.isEmpty()) {
        throw InputError.build("username", "can't be blank")
    }

    return userTable.getItem(key(username))
            ?: throw InputError.build("username", "not found")
}

data class GetCurrentUserResult(val user: User, val token: String)

fun getCurrentUser(auth: String?): GetCurrentUserResult {
    val (username, token) = verifyAuthorization(auth)

    val user = getUserByUsername(username)

    return GetCurrentUserResult(user, token)
}
