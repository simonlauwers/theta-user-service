package com.theta.userservice.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class LoginDTO {
    @get:Email(message = "Not a valid email!")
    val email = ""
    @get:NotBlank(message = "Password should not be blank!")
    val password =""
}