package com.theta.userservice.model

import lombok.Getter
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*


@Entity
class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name = "token_id", columnDefinition = "VARCHAR(36)")
    val tokenId: UUID = UUID.randomUUID()

    @Column(name = "confirmation_token", unique = true)
    var confirmationToken: String? = null

    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date? = null

    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", columnDefinition = "VARCHAR(36)")
    var userEntity: User? = null

    constructor() {}
    constructor(userEntity: User?) {
        this.userEntity = userEntity
        createdDate = Date()
        confirmationToken = UUID.randomUUID().toString()
    } // getters and setters
}