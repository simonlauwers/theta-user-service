package com.theta.userservice.service

import com.theta.userservice.dto.EditProfileDTO
import com.theta.userservice.model.User
import com.theta.userservice.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userRepository: UserRepository) {
    fun save(user: User): User {
        return userRepository.save(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun findById(id: UUID) : Optional<User> {
        return userRepository.findById(id)
    }

    fun editProfile(editProfileDTO: EditProfileDTO) : User?{
        val user = findByEmail(editProfileDTO.email)
        if(user != null){
            user.displayName = editProfileDTO.displayName
            user.profilePicture = editProfileDTO.profilePicture
            return userRepository.save(user)
        }
        return null

    }

    /* FOR TESTING PURPOSES ONLY!!! */
    fun deleteAll(){
        return userRepository.deleteAll();
    }
}