package com.theta.userservice.service

import com.theta.userservice.domain.exceptions.JwtEmailMismatchException
import com.theta.userservice.domain.exceptions.UnauthorizedException
import com.theta.userservice.domain.exceptions.UserDisplayNameConflict
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.theta.userservice.domain.model.User
import io.jsonwebtoken.*
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.SecretKey
import javax.persistence.EntityNotFoundException

@Service
class JwtService(val userService: UserService) {

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
        val issuer = user.userId.toString()
        return Jwts.builder()
                .setIssuer(issuer)
                .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // 1 day
                .signWith(secretKey)
                .compact()
    }

    fun getJwtClaims(cookieValue: String): Jws<Claims> {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(cookieValue)
    }

    fun whoAmI(jwt: String?) : User{
        if (jwt == null) {
            throw UnauthorizedException("user/unauthorized")
        }
        val body = getJwtClaims(jwt).body
        val user = userService.findById(UUID.fromString(body.issuer))
        return if (user.isPresent) {
            user.get()
        } else {
            throw EntityNotFoundException("user/not-found")
        }
    }

    fun checkJwtWithUser(jwt: String?, displayName: String, email: String) {
        if (jwt == null) {
            throw UnauthorizedException("user/unauthorized")
        }
        val jwtUser = userService.findById(UUID.fromString(getJwtClaims(jwt).body.issuer))
        val jwtEmail = jwtUser.map(User::email).orElse("")
        if (email != jwtEmail) {
            throw JwtEmailMismatchException("user/jwt-email-mismatch")
        }
        if (userService.findByDisplayName(displayName) != null) {
            throw UserDisplayNameConflict("user/display-name-conflict")
        }
    }
}