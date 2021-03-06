package com.theta.userservice.domain.model

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity(name ="reset_password_tokens")
class ResetPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name = "token_id", columnDefinition = "VARCHAR(36)")
    val tokenId: UUID = UUID.randomUUID()

    @Column(unique = true)
    var resetPasswordToken: String? = null

    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date? = null

    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
    @JoinColumn(nullable = false, name = "user_id", columnDefinition = "VARCHAR(36)")
    var userEntity: User? = null

    constructor() {}
    constructor(userEntity: User?) {
        this.userEntity = userEntity
        createdDate = Date()
        resetPasswordToken = UUID.randomUUID().toString()
    } // getters and setters
}