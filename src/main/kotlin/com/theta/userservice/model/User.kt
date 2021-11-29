package com.theta.userservice.model

import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.Type
import org.hibernate.validator.constraints.Length
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Entity
@Getter
@Setter
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name = "user_id", columnDefinition = "VARCHAR(36)")
    val userId: UUID = UUID.randomUUID()

    @Column(unique = true)
    @get:Email
    var email = ""

    @Column
    @NotBlank
    var password = ""

    @Column(unique = true)
    @Length(min = 6)
    var displayName = ""

    var profilePicture = "https://commons.wikimedia.org/wiki/File:Default_pfp.jpg"

    var isEnabled = false;
}
