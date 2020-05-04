package com.serverless.service

import com.serverless.model.EmailUser
import com.serverless.model.InputError
import com.serverless.model.User
import com.serverless.model.verifyAuthorization
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException

fun putUser(user: User) {
    user.validate()

    val emailUser = EmailUser(
            email = user.email,
            username = user.username
    )

    try {
        ddbClient.transactWriteItems {
            it.put(userTable) {
                item(user)
                conditionExpression(attributeNotExists("username"))
            }
            it.put(emailUserTable) {
                item(emailUser)
                conditionExpression(attributeNotExists("email"))
            }
        }
    } catch (e: TransactionCanceledException) {
        throwInputError(e) {
            InputError.build(
                    "username" to listOf("has already been taken"),
                    "email" to listOf("has already been taken")
            )
        }
    }
}

fun updateUser(oldUser: User, newUser: User) {
    newUser.validate()

    val transaction = TransactWriteItemsEnhancedRequest.builder()

    if (oldUser.email != newUser.email) {
        val newEmailUser = EmailUser(
                email = newUser.email,
                username = newUser.username
        )

        transaction.put(emailUserTable) {
            item(newEmailUser)
            conditionExpression(attributeNotExists("email"))
        }

        transaction.delete(emailUserTable) {
            key(oldUser.email)
            conditionExpression(attributeExists("email"))
        }
    }

    transaction.put(userTable) {
        item(newUser)
        conditionExpression(attributeEquals("email", oldUser.email))
    }

    try {
        ddbClient.transactWriteItems(transaction.build())
    } catch (e: TransactionCanceledException) {
        throwInputError(e) {
            InputError.build("email", "has already been taken")
        }
    }
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

fun getUserByUsername(username: String?): User {
    if (username.isNullOrEmpty()) {
        throw InputError.build("username", "can't be blank")
    }

    return userTable.getItem(key(username))
            ?: throw InputError.build("username", "not found")
}

data class GetCurrentUserResult(val user: User?, val token: String?)

fun getCurrentUser(auth: String?): GetCurrentUserResult {
    val (username, token) = verifyAuthorization(auth)
    if (username == null) {
        return GetCurrentUserResult(null, token)
    }

    val user = getUserByUsername(username)

    return GetCurrentUserResult(user, token)
}
