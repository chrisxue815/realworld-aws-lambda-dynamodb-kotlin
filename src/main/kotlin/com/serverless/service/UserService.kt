package com.serverless.service

import com.serverless.model.EmailUser
import com.serverless.model.InputError
import com.serverless.model.User
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException

fun putUser(user: User): InputError? {
    val err = user.validate()
    if (err != null) {
        return err
    }

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
            return mapOf(
                    "username" to listOf("has already been taken"),
                    "email" to listOf("has already been taken")
            )
        } else {
            throw e
        }
    }

    return null
}
