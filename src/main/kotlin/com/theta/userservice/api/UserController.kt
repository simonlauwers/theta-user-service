package com.theta.userservice.api

import Sl4jLogger
import Sl4jLogger.Companion.log
import com.theta.userservice.dto.LoginDTO
import com.theta.userservice.dto.MessageDTO
import com.theta.userservice.dto.RegisterDTO
import com.theta.userservice.model.User
import com.theta.userservice.service.JwtService
import com.theta.userservice.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@RequestMapping("/api")
@Sl4jLogger
class UserController(val userService: UserService, val jwtService: JwtService) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody body: RegisterDTO): ResponseEntity<Any> {
        val user = User();
        user.displayName = body.displayName;
        user.email = body.email;
        user.password = BCryptPasswordEncoder().encode(body.password);

        return if (userService.findByEmail(user.email) != null)
            ResponseEntity("User already exists", HttpStatus.CONFLICT)
        else {
            ResponseEntity.ok(userService.create(user))
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        val user = userService.findByEmail(body.email)
                ?: return ResponseEntity.badRequest().body(MessageDTO("User not found!"));
        val responseHeaders = HttpHeaders()
        if (!BCryptPasswordEncoder().matches(body.password, user.password))
            return ResponseEntity.badRequest().body(MessageDTO("Invalid password!"))
        else {
            val jwt = jwtService.create(user);
            val cookie = Cookie ("jwt", jwt);
            cookie.isHttpOnly = true;
            response.addCookie(cookie)
            return ResponseEntity.ok(MessageDTO("Success! Jwt cookie created!"))
        }
    }

    @GetMapping("/test")
    fun test(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body(MessageDTO("unauthenticated"))
            }
            val body = jwtService.getJwtClaims(jwt).body;
            log.info(body.issuer);

            val user = userService.findById(UUID.fromString(body.issuer));

            return if (user.isPresent) {
                ResponseEntity.ok(user);
            } else {
                ResponseEntity.badRequest().body(MessageDTO("Couldn't find a user from that token"));
            }
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(MessageDTO("unauthenticated"))
        }
    }

    /**
     * On logout just expire the token on client side
     * This doesnt require a endpoint on the server.
     * */
}