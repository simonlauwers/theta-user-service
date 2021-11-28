package com.theta.userservice.model

import org.hibernate.annotations.Type
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Email


@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name = "ID", columnDefinition = "VARCHAR(36)")
    val uuid: UUID = UUID.randomUUID()

    @Column(unique = true)
    var email = ""

    @Column
    @get:NotBlank(message = "Password should not be blank!")
    var password = ""

    @Column(unique = true)
    var displayName = ""

}
