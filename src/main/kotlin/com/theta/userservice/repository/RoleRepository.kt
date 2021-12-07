package com.theta.userservice.repository

import com.theta.userservice.domain.model.Role
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RoleRepository : CrudRepository<Role, UUID> {
    fun findByName(name: String) : Role?
}