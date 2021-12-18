package com.theta.userservice.domain.service

import com.theta.userservice.domain.model.Role
import com.theta.userservice.repository.RoleRepository
import org.springframework.stereotype.Service

@Service
class RoleService(val roleRepository: RoleRepository) {
    fun saveRole(role: Role){
        if(roleRepository.findByName(role.name) == null){
            roleRepository.save(role)
        }
    }

    fun findByName(name: String) : Role? {
        return roleRepository.findByName(name)
    }
}