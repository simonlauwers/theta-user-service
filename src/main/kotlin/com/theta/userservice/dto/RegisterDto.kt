package com.theta.userservice.dto

import com.theta.userservice.domain.model.Provider

class RegisterDto (
        val displayName: String,
        val email: String,
        var password: String,
        val profilePicture: String,
        val provider: Provider
)