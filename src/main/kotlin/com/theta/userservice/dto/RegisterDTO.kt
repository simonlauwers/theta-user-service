package com.theta.userservice.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class RegisterDTO (
        val displayName: String = "",
        @get:Email(message = "Not a valid email!")
        val email: String = "",
        @get:NotBlank(message = "Password should not be blank!")
        val password: String = ""
)