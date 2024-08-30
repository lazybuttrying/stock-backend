package com.dyk.test.config.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtUtil {

    private var commonHeader = Jwts.header()
        .keyId("aKeyId")
        .build();

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.expire}")
    private var expiration: Long = 0

    private fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(this.secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(username: String): String {
        val now = Date()
        val validity = Date(now.time + expiration)

        return Jwts.builder()
//            .header()
//                .add(commonHeader)
//                .add("HelloHeader", "hi")
            //                .and()
            .subject(username)
            .expiration(validity)
            .issuedAt(now)
            .signWith(getSignInKey())
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims?.expiration?.before(Date())!!
        } catch (e: Exception) {
            false
        }
    }

    fun getUsername(token: String): String? {
        return getClaims(token)?.subject
    }

    private fun getClaims(token: String): Claims? {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parse(token)
            .payload as Claims
    }

}
