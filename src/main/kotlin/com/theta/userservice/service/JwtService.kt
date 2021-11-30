package com.theta.userservice.service

import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.theta.userservice.model.User
import io.jsonwebtoken.*
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.SecretKey

@Service
class JwtService {

    @Value("\${jwt.expiration}")
    val expiration: Int = 604800 // 1 week

    @Value("\${jwt.secret}")
    private val secret: String = ""

    lateinit var secretKey: SecretKey

    @PostConstruct
    fun init() {
        secretKey = Keys.hmacShaKeyFor(secret.toByteArray())
    }

    // we could use a different jwt per different endpoint, each with his own secret key and exp time
    fun create(user: User): String {
        val issuer = user.userId.toString();
        return Jwts.builder()
                .setIssuer(issuer)
                .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // 1 day
                .signWith(secretKey)
                .compact()
    }

    fun getJwtClaims(cookieValue: String): Jws<Claims> {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(cookieValue);
    }

}