package com.theta.userservice.model

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@NoArgsConstructor
@AllArgsConstructor

class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name = "ID", columnDefinition = "VARCHAR(36)")
    val uuid: UUID = UUID.randomUUID()

    @Column(unique = true)
    @get:Email(message = "Not a valid email!")
    var email = ""

    @Column
    @get:NotBlank(message = "Password should not be blank!")
    var password = ""

    @Column(unique = true)
    var displayName = ""

    @Column
    var profilePictureSrc = "https://upload.wikimedia.org/wikipedia/commons/8/89/Portrait_Placeholder.png" //default profile picture

}
