package com.theta.userservice.model

import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Getter
@Setter
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
    @get:NotBlank(message = "Displayname should not be blank!!")
    var displayName = ""

    var profilePicture = "https://commons.wikimedia.org/wiki/File:Default_pfp.jpg"
}
