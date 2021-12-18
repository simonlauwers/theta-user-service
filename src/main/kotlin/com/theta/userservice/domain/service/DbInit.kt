package com.theta.userservice.domain.service

import com.theta.userservice.domain.model.Role
import com.theta.userservice.domain.model.User
import com.theta.userservice.domain.service.RoleService
import com.theta.userservice.domain.service.UserService
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
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!, "https://pbs.twimg.com/profile_images/1331347002119090179/HAIk7lN2_400x400.jpg"))
        }
        if(userService.findByEmail("simon.lauwers@telenet.be") == null){
            userService.save(User("simon.lauwers@telenet.be", BCryptPasswordEncoder().encode("admin"), "Simon Lauwers",
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!, "https://mir-s3-cdn-cf.behance.net/user/276/88bd3446525599.57e4284f2899b.jpg"))
        }
    }
}