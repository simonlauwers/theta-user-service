package com.theta.userservice.repository

import com.theta.userservice.model.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, UUID> {
    fun findByEmail(email: String) : Optional<User>
    fun findByEmailAndPassword(email: String, password: String) : Optional<User>
}