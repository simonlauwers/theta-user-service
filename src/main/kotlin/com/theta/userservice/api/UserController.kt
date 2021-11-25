package com.theta.userservice.api

import com.theta.userservice.dto.RegisterDTO
import com.theta.userservice.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.theta.userservice.model.User
import com.theta.userservice.service.UserService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("api")
class UserController(val userService: UserService, val jwtService: JwtService) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody body: RegisterDTO): ResponseEntity<Any> {
        val user = User();
        user.displayName = body.displayName;
        user.email = body.email;
        user.password = body.password;

        val existingUser = userService.findByEmail(user.email)
        return if (userService.findByEmail(user.email).isPresent)
            ResponseEntity("User already exists", HttpStatus.CONFLICT)
        else {
            val createdUser = userService.create(user)
            jwtService.create(createdUser)
            ResponseEntity.ok(createdUser)
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody user: User): ResponseEntity<Any> {
        return if (userService.findByEmailAndPassword(user.email, user.password).isPresent)
            ResponseEntity.ok(jwtService.create(user))
        else
            ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("Test ok")
    }
}