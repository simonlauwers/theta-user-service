package com.theta.userservice.service

import com.theta.userservice.model.User
import com.theta.userservice.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userRepository: UserRepository) {
    fun create(user: User): User {
        return userRepository.save(user)
    }

    fun findByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    fun findByEmailAndPassword(email: String, pw: String): Optional<User> {
        return if (pw.isEmpty())
            Optional.empty()
        else
            userRepository.findByEmailAndPassword(email, encryptPassword(pw))
    }

    fun encryptPassword(pw: String): String {
        return BCryptPasswordEncoder().encode(pw);
    }
}