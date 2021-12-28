package com.theta.userservice.service

import Sl4jLogger.Companion.log
import com.theta.userservice.domain.exceptions.*
import com.theta.userservice.domain.model.Provider
import com.theta.userservice.domain.model.User
import com.theta.userservice.dto.*
import com.theta.userservice.repository.UserRepository
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.RandomStringUtils
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
class UserService(val userRepository: UserRepository, val roleService: RoleService, val jwtService: JwtService) {
    fun save(user: User): User {
        log.info("user found: " + (findByEmail(user.email)?.userId ?: ""))
        return findByEmail(user.email) ?: userRepository.save(user)
    }

    fun update(user: User): User {
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
        if (registerDto.provider != Provider.LOCAL) {
            val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?"
            user.password = RandomStringUtils.random(10, characters)
        } else {
            user.password = BCryptPasswordEncoder().encode(registerDto.password)
        }
        user.displayName = registerDto.displayName
        user.email = registerDto.email
        user.role = roleService.findByName("user")!!
        user.isBanned = false
        user.provider = registerDto.provider

        if (registerDto.profilePicture.isBlank()) {
            user.profilePicture = "https://rogroep.nl/wp-content/uploads/2020/09/blank-profile-picture-973460_640.png"
        } else {
            user.profilePicture = registerDto.profilePicture
        }

        return if (findByEmail(user.email) != null)
            throw UserEmailConflictException("user/email-conflict")
        else if (findByDisplayName(user.displayName) != null) {
            throw UserDisplayNameConflict("user/display-name-conflict")
        } else {
            log.info("user " + user.email + " has been registered!")
            save(user)
        }
    }

    fun login(loginDto: LoginDto, response: HttpServletResponse): User {
        val user = findByEmail(loginDto.email)
                ?: throw EntityNotFoundException("user/not-found")
        if (!user.isEnabled && user.provider == Provider.LOCAL)
            throw UserNotConfirmedException("user/not-confirmed")
        if (user.isBanned) {
            throw UserIsBannedException("user/banned")
        }
        return if (!BCryptPasswordEncoder().matches(loginDto.password, user.password) && user.provider == Provider.LOCAL)
            throw InvalidPasswordException("user/invalid-password")
        else {
            user.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            save(user)
            val jwt = jwtService.create(user)
            val cookie = Cookie("jwt", jwt)
            cookie.isHttpOnly = true
            cookie.domain = "theta-risk.com"
            response.addCookie(cookie)
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

    fun whoAmI(jwt: String?): User {
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

    fun googleLogin(user: GoogleProfileDto, response: HttpServletResponse): User {
        var exUser = findByEmail(user.email)
        if (exUser == null) {
            exUser = registerUser(RegisterDto(user.googleId, user.email, "", user.imageUrl, Provider.GOOGLE))
        }
        return login(LoginDto(exUser.email, exUser.password), response)
    }

    fun displayNameAvailale(displayName: DisplaynameDto): Boolean {
        if(findByDisplayName(displayName.displayName) == null) return true else throw UserDisplayNameConflict("user/display-name-conflict")
    }

}