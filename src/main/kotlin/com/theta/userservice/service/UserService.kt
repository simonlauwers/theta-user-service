package com.theta.userservice.service

import Sl4jLogger.Companion.log
import com.theta.userservice.api.controllers.messaging.MessageSender
import com.theta.userservice.domain.exceptions.*
import com.theta.userservice.domain.model.User
import com.theta.userservice.dto.*
import com.theta.userservice.repository.UserRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Service
@Slf4j
class UserService(val userRepository: UserRepository, val roleService: RoleService, val jwtService: JwtService, val messageSender: MessageSender) {
    fun save(user: User): User {
        return findByEmail(user.email) ?: userRepository.save(user)
    }

    fun update(user:User) : User{
        return userRepository.save(user)
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
            messageSender.sendUser(GameUserDto(user.userId));
            log.info("user " + user.email + " has been registered!")
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
            messageSender.sendUser(AnalyticsUserDto(user.userId, LocalDateTime.parse(user.lastLogin, DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            log.info("user " + user.email + " succesfully logged in!")
            return user
        }
    }

    fun editProfile(editProfileDto: EditProfileDto, jwt: String?): User {
        checkJwtWithUser(jwt, editProfileDto.displayName, editProfileDto.email)
        val user = findByEmail(editProfileDto.email) ?: throw EntityNotFoundException("user/not-found")
        user.displayName = editProfileDto.displayName
        user.profilePicture = editProfileDto.profilePicture
        log.info("userprofile " + user.email + " was edited!")
        return userRepository.save(user)
    }

    fun whoAmI(jwt: String?) : User{
        if (jwt == null) {
            throw UnauthorizedException("user/unauthorized")
        }
        val body = jwtService.getJwtClaims(jwt).body
        val user = findById(UUID.fromString(body.issuer))
        return if (user.isPresent) {
            user.get()
        } else {
            log.info("you are user: " + user.get().displayName)
            throw EntityNotFoundException("user/not-found")
        }
    }

    fun checkJwtWithUser(jwt: String?, displayName: String, email: String) {
        if (jwt == null) {
            throw UnauthorizedException("user/unauthorized")
        }
        val jwtUser = findById(UUID.fromString(jwtService.getJwtClaims(jwt).body.issuer))
        val jwtEmail = jwtUser.map(User::email).orElse("")
        if (email != jwtEmail) {
            throw JwtEmailMismatchException("user/jwt-email-mismatch")
        }
        if (findByDisplayName(displayName) != null) {
            throw UserDisplayNameConflict("user/display-name-conflict")
        }
    }
}