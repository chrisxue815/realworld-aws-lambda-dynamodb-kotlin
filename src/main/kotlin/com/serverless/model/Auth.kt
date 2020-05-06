package com.serverless.model

import com.lambdaworks.crypto.SCrypt
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.spec.SecretKeySpec

const val TOKEN_EXPIRATION_DAYS = 60L

val PASSWORD_SALT = "KU2YVXA7BSNExJIvemcdz61eL86IJDCC".toByteArray()
val JWT_SECRET = "C92cw5od80NCWIvu4NZ8AKp5NyTbnBmG".toByteArray()
val JWT_ALG = SignatureAlgorithm.HS256
val JWT_KEY = SecretKeySpec(JWT_SECRET, JWT_ALG.jcaName)

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

data class VerifyAuthorizationResult(val username: String?, val token: String?)

fun verifyAuthorization(auth: String?): VerifyAuthorizationResult {
    if (auth == null) {
        return VerifyAuthorizationResult(null, null)
    }

    val parts = auth.split(' ', limit = 2)
    if (parts.size != 2 || parts[0] != "Token") {
        return VerifyAuthorizationResult(null, null)
    }

    val token = parts[1]
    val username = verifyToken(token)
    return VerifyAuthorizationResult(username, token)
}

private fun verifyToken(tokenString: String): String? {
    val token = try {
        Jwts.parserBuilder().setSigningKey(JWT_KEY).build().parseClaimsJws(tokenString)
    } catch (e: JwtException) {
        return null
    }

    if (token.header.getAlgorithm() != JWT_ALG.value) {
        return null
    }

    val now = Instant.now()
    if (now.isAfter(token.body.expiration.toInstant())) {
        return null
    }

    val username = token.body.subject
    if (username.isNullOrEmpty()) {
        return null
    }

    return username
}
