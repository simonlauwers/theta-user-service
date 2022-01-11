package com.theta.userservice.controller.dto

import com.theta.userservice.domain.model.Provider
import com.theta.userservice.domain.model.Role
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class UserDto {
    var userId: UUID = UUID.randomUUID()

    var email = ""

    var displayName = ""

    var profilePicture = "https://avatars.dicebear.com/api/micah/${Math.random()}.svg"

    var isEnabled = false

    var isBanned = false

    var lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    var provider: Provider = Provider.LOCAL

    var role: Role? = null

    constructor(email:String, displayName: String, isEnabled: Boolean, isBanned: Boolean, provider: Provider, role: Role, profilePicture: String, lastLogin: String) {
        this.email = email
        this.displayName = displayName
        this.isEnabled = isEnabled
        this.isBanned = isBanned
        this.provider = provider
        this.role= role
        this.profilePicture = profilePicture
        this.lastLogin = lastLogin
    }
}