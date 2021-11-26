package com.theta.userservice.model

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Email


@Entity
class User {
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()

    @Column(unique = true)
    var email = ""

    @Column
    @get:NotBlank(message = "Password should not be blank!")
    var password = ""
        get() = field
        set(value) {
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value)
        }


    @Column(unique = true)
    var displayName = ""

}
