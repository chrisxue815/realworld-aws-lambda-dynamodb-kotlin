package com.serverless.model

import com.lambdaworks.crypto.SCrypt
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.spec.SecretKeySpec

const val TOKEN_EXPIRATION_DAYS = 60L

val PASSWORD_SALT = "KU2YVXA7BSNExJIvemcdz61eL86IJDCC".toByteArray()
val JWT_SECRET = "C92cw5od80NCWIvu4NZ8AKp5NyTbnBmG".toByteArray()
val JWT_KEY = SecretKeySpec(JWT_SECRET, SignatureAlgorithm.HS256.jcaName)

fun scrypt(password: String): ByteArray {
    return SCrypt.scrypt(password.toByteArray(), PASSWORD_SALT, 32768, 8, 1, PASSWORD_HASH_LENGTH)
}

fun generateToken(username: String): String {
    val exp = Instant.now().plus(TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS)
    val jwt = Jwts.builder()
            .setSubject(username)
            .setExpiration(Date.from(exp))
            .signWith(JWT_KEY)
    return jwt.compact()
}
