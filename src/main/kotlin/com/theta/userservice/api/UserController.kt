package com.theta.userservice.api

import Sl4jLogger
import Sl4jLogger.Companion.log
import com.theta.userservice.dto.LoginDTO
import com.theta.userservice.dto.Message
import com.theta.userservice.dto.RegisterDTO
import com.theta.userservice.model.User
import com.theta.userservice.service.JwtService
import com.theta.userservice.service.UserService
import org.apache.coyote.Response
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
                ?: return ResponseEntity.badRequest().body(Message("User not found!"));
        val responseHeaders = HttpHeaders()
        if (!BCryptPasswordEncoder().matches(body.password, user.password))
            return ResponseEntity.badRequest().body(Message("Invalid password!"))
        else
            responseHeaders.set("Authorization", jwtService.create(user))
            return ResponseEntity.ok().headers(responseHeaders).body(Message("Success! Jwt is available in Authorization-header"));
    }

    @GetMapping("/test")
    fun test(@RequestHeader("Authorization") jwt: String?): ResponseEntity<Any> {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body(Message("unauthenticated"))
            }
            val body = jwtService.getJwtClaims(jwt).body;
            log.info(body.issuer);

            val user = userService.findById(UUID.fromString(body.issuer));

            if(user.isPresent){
                return ResponseEntity.ok(user);
            }else{
                return ResponseEntity.badRequest().body(Message("Couldnt find a user bound to that token"));
            }
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(Message("unauthenticated"))
        }
    }

    /**
     * On logout just expire the token on client side
     * This doesnt require a endpoint on the server.
     * */
}