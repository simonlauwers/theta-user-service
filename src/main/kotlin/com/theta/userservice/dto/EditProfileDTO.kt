package com.theta.userservice.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class EditProfileDTO {
    @get:Email(message = "Not a valid email!")
    val email = ""
    @get:NotBlank(message = "Displayname should not be blank!!")
    val displayName =""

    val profilePicture = "https://commons.wikimedia.org/wiki/File:Default_pfp.jpg"

}