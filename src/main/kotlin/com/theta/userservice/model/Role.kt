package com.theta.userservice.model

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity(name ="roles")
class Role {
    @Column(unique = true)
    var name: String = "user"

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name = "ID", columnDefinition = "VARCHAR(36)")
    val roleId: UUID = UUID.randomUUID()

    constructor(name: String){
        this.name = name
    }
    constructor(){
    }
}