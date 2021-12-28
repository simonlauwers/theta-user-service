package com.theta.userservice.domain.service

import com.theta.userservice.domain.model.Provider
import com.theta.userservice.domain.model.Role
import com.theta.userservice.domain.model.User
import com.theta.userservice.domain.service.RoleService
import com.theta.userservice.domain.service.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class DbInit(val roleService: RoleService, val userService: UserService) {
    @PostConstruct
    fun seed() {
        roleService.saveRole(Role("admin"))
        roleService.saveRole(Role("user"))

        // seed users
        if(userService.findByEmail("quinten@verhelst.dev") == null){
            userService.save(User(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1"), "quinten@verhelst.dev", BCryptPasswordEncoder().encode("admin"), "quintenvh",
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!, "https://pbs.twimg.com/profile_images/1331347002119090179/HAIk7lN2_400x400.jpg", Provider.LOCAL))
            userService.save(User(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2"), "simon.lauwers@student.kdg.be", BCryptPasswordEncoder().encode("admin"), "simonlauw",
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!, "", Provider.LOCAL))
            userService.save(User(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3"), "nathan.tetroashvili@student.kdg.be", BCryptPasswordEncoder().encode("admin"), "nathantetro",
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!, "", Provider.LOCAL))
            userService.save(User(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4"), "siemen.vandemosselaer@student.kdg.be", BCryptPasswordEncoder().encode("admin"), "siemenvdm",
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!, "", Provider.LOCAL))
            userService.save(User(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5"), "youssef.taouil@student.kdg.be", BCryptPasswordEncoder().encode("admin"), "yousseftao",
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!, "", Provider.LOCAL))
            userService.save(User(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6"), "daniel.savin@student.kdg.be", BCryptPasswordEncoder().encode("admin"), "danielsav",
                    isEnabled = true, isBanned = false, roleService.findByName("admin")!!, "", Provider.LOCAL))
        }
    }
}