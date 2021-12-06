package com.theta.userservice.service

import com.theta.userservice.domain.exceptions.*
import com.theta.userservice.domain.model.User
import com.theta.userservice.dto.*
import com.theta.userservice.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Service
class UserService(val userRepository: UserRepository, val roleService: RoleService, val jwtService: JwtService) {
    fun save(user: User): User {
        return findByEmail(user.email) ?: userRepository.save(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun findByDisplayName(name: String): User? {
        return userRepository.findByDisplayName(name)
    }

    fun findById(id: UUID): Optional<User> {
        return userRepository.findById(id)
    }

    fun deleteUser(user: User) {
        userRepository.delete(user)
    }

    fun registerUser(registerDto: RegisterDto): User {
        val user = User()
        user.displayName = registerDto.displayName
        user.email = registerDto.email
        user.password = BCryptPasswordEncoder().encode(registerDto.password)
        user.role = roleService.findByName("user")!!

        return if (findByEmail(user.email) != null)
            throw UserEmailConflictException("user/email-conflict")
        else if (findByDisplayName(user.displayName) != null) {
            throw UserDisplayNameConflict("user/display-name-conflict")
        } else {
            save(user)
        }
    }

    fun login(loginDto: LoginDto, response: HttpServletResponse): User {
        val user = findByEmail(loginDto.email)
                ?: throw EntityNotFoundException("user/not-found")
        if (!user.isEnabled)
            throw UserNotConfirmedException("user/not-confirmed")
        if (user.isBanned) {
            throw UserIsBannedException("user/banned")
        }
        return if (!BCryptPasswordEncoder().matches(loginDto.password, user.password))
            throw InvalidPasswordException("user/invalid-password")
        else {
            user.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            save(user)
            val jwt = jwtService.create(user)
            val cookie = Cookie("jwt", jwt)
            cookie.isHttpOnly = true
            response.addCookie(cookie)
            return user
        }
    }

    fun editProfile(editProfileDto: EditProfileDto, jwt: String?): User {
        jwtService.checkJwtWithUser(jwt, editProfileDto.displayName, editProfileDto.email)
        val user = findByEmail(editProfileDto.email) ?: throw EntityNotFoundException("user/not-found")
        user.displayName = editProfileDto.displayName
        user.profilePicture = editProfileDto.profilePicture
        return userRepository.save(user)
    }
}