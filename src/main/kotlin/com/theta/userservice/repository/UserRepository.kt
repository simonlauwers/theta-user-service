package com.theta.userservice.repository

import com.theta.userservice.model.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, UUID> {
    fun findByEmail(email: String) : User?
    fun findByDisplayName(name: String) : User?
}