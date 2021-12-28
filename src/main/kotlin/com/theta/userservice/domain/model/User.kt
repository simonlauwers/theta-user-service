package com.theta.userservice.domain.model

import lombok.AllArgsConstructor
import org.hibernate.annotations.Type
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Email


@Entity(name = "users")
@AllArgsConstructor
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name = "ID", columnDefinition = "VARCHAR(36)")
    var userId: UUID = UUID.randomUUID()

    @Column(unique = true)
    @get:Email(message = "Not a valid email!")
    var email = ""

    @Column
    @get:NotBlank(message = "Password should not be blank!")
    var password = ""

    @Column(unique = true)
    @Length(min = 6)
    var displayName = ""

    @Column
    var profilePicture = "https://commons.wikimedia.org/wiki/File:Default_pfp.jpg"

    @Column
    var isEnabled = false

    @Column
    var isBanned = false

    @Column
    var lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @Column
    var provider: Provider = Provider.LOCAL

    @OneToOne(targetEntity = Role::class, fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
    @JoinColumn(nullable = false, name = "role_id", columnDefinition = "VARCHAR(36)")
    var role: Role? = null

    constructor(email:String, password:String, displayName: String, isEnabled: Boolean, isBanned: Boolean, provider: Provider) {
        this.email = email
        this.password = password
        this.displayName = displayName
        this.isEnabled = isEnabled
        this.isBanned = isBanned
        this.provider = provider
    }
    constructor(userId: UUID, email:String, password:String, displayName: String, isEnabled: Boolean, isBanned: Boolean, role: Role, profilePicture: String, provider: Provider) {
        this.userId = userId
        this.email = email
        this.password = password
        this.displayName = displayName
        this.isEnabled = isEnabled
        this.isBanned = isBanned
        this.role = role
        this.profilePicture = profilePicture
        this.provider =  provider
    }
    constructor(){
    }
}
