package com.theta.userservice.database

import com.theta.userservice.model.Role
import com.theta.userservice.model.User
import com.theta.userservice.service.RoleService
import com.theta.userservice.service.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DbInit(val roleService: RoleService, val userService: UserService) {
    @PostConstruct
    fun seed() {
        roleService.saveRole(Role("admin"))
        roleService.saveRole(Role("user"))

        // seed admin user
        if(userService.findByEmail("quinten@verhelst.dev") == null){
            userService.save(User("quinten@verhelst.dev", BCryptPasswordEncoder().encode("admin"), "Quinten",
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!))
        }
    }
}