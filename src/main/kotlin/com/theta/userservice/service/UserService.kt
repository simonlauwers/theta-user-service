package com.theta.userservice.service

import com.theta.userservice.model.User
import com.theta.userservice.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userRepository: UserRepository) {
    fun create(user: User): User {
        return userRepository.save(user);
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email);
    }

    fun findById(id: UUID) : Optional<User> {
        return userRepository.findById(id);
    }




}