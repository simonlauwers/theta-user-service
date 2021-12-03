package com.theta.userservice.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class RegisterDTO (
        val displayName: String,
        val email: String,
        val password: String,
        val profilePicture: String = "https://commons.wikimedia.org/wiki/File:Default_pfp.jpg"
)