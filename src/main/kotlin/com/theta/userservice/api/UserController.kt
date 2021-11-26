package com.theta.userservice.api

import com.theta.userservice.dto.LoginDTO
import com.theta.userservice.dto.RegisterDTO
import com.theta.userservice.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.theta.userservice.model.User
import com.theta.userservice.service.UserService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/api")
class UserController(val userService: UserService, val jwtService: JwtService) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody body: RegisterDTO): ResponseEntity<Any> {
        val user = User();
        user.displayName = body.displayName;
        user.email = body.email;
        user.password = body.password;

        return if (userService.findByEmail(user.email).isPresent)
            ResponseEntity("User already exists", HttpStatus.CONFLICT)
        else {
            val createdUser = userService.create(user)
            jwtService.create(createdUser)
            ResponseEntity.ok(createdUser)
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody body: LoginDTO): ResponseEntity<Any> {
        val user = User()
        user.password = body.password
        user.email = body.email
        return if (userService.findByEmailAndPassword(body.email, body.password).isPresent)
            ResponseEntity.ok(jwtService.create(user))
        else
            ResponseEntity(HttpStatus.BAD_GATEWAY)
    }

    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("Test ok")
    }
}