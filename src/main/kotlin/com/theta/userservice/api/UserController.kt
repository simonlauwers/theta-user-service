package com.theta.userservice.api

import Sl4jLogger
import Sl4jLogger.Companion.log
import com.theta.userservice.dto.*
import com.theta.userservice.model.ConfirmationToken
import com.theta.userservice.model.ResetPasswordToken
import com.theta.userservice.model.User
import com.theta.userservice.service.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@Sl4jLogger
class UserController(val userService: UserService, val jwtService: JwtService, val emailService: EmailService, val confirmationTokenService: ConfirmationTokenService, val resetPasswordTokenService: ResetPasswordTokenService) {
    @CrossOrigin
    @PostMapping("/register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<Any> {
        try {
            val user = User()
            user.displayName = body.displayName
            user.email = body.email
            user.password = BCryptPasswordEncoder().encode(body.password)

            return if (userService.findByEmail(user.email) != null)
                ResponseEntity.status(409).body(MessageDTO("user/email-conflict", 409, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
            else if (userService.findByDisplayName(user.displayName) != null) {
                ResponseEntity.status(409).body(MessageDTO("user/display-name-conflict", 409, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
            } else {
                val newUser = userService.save(user)
                return ResponseEntity.ok(newUser)
            }
        } catch (e: Exception) {
            return ResponseEntity.status(400).body(MessageDTO("user/malformed-body", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        }
    }

    @CrossOrigin
    @PostMapping("/confirm-account")
    fun confirmAccount(@RequestBody token: String): ResponseEntity<Any> {
        val confirmationToken = confirmationTokenService.findByConfirmationToken(token)
                ?: return ResponseEntity.badRequest().body(MessageDTO("confirmationtoken/not-found", 200, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()));

        val userToUpdate = userService.findById(confirmationToken.userEntity!!.userId)
        userToUpdate.get().isEnabled = true;
        val updatedUser = userService.save(userToUpdate.get())
        return ResponseEntity.ok(updatedUser)

    }

    @CrossOrigin
    @PostMapping("/send-confirmation-email")
    fun sendConfirmationEmail(@RequestBody email: String): ResponseEntity<Any> {
        try {
            val user = userService.findByEmail(email)
                    ?: return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
            val confirmationToken = ConfirmationToken(user)
            confirmationTokenService.addConfirmationToken(confirmationToken)
            val link = "https://theta-risk.com/game/confirm?token="
            val msg = "<h1>Hello, ${user.displayName}!</h1><br><p>Confirm your account in the next 24hr using this ${link}${confirmationToken}</p><p>Token for development testing: ${confirmationToken.confirmationToken}"
            emailService.sendMail("no-reply@theta-risk.com", email, "Confirm your account!", msg)

            return ResponseEntity.status(200).body(MessageDTO("email/confirmation-sent", 200, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        } catch (e: Exception) {
            return ResponseEntity.status(400).body(MessageDTO("user/invalid-email", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        }
    }

    @CrossOrigin
    @PostMapping("/login")
    fun login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        val user = userService.findByEmail(body.email)
                ?: return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        if (!user.isEnabled)
            return ResponseEntity.badRequest().body(MessageDTO("user/not-confirmed", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        val responseHeaders = HttpHeaders()
        return if (!BCryptPasswordEncoder().matches(body.password, user.password))
            ResponseEntity.badRequest().body(MessageDTO("user/invalid-password", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        else {
            val jwt = jwtService.create(user)
            val cookie = Cookie("jwt", jwt)
            cookie.isHttpOnly = true
            response.addCookie(cookie)
            return ResponseEntity.ok(user)
        }
    }

    @CrossOrigin
    @GetMapping("/whoami")
    fun test(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body(MessageDTO("user/unauthorized", 401, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
            }
            val body = jwtService.getJwtClaims(jwt).body
            log.info(body.issuer)

            val user = userService.findById(UUID.fromString(body.issuer))

            return if (user.isPresent) {
                ResponseEntity.ok(user)
            } else {
                return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
            }
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(MessageDTO("user/unauthorized", 401, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        }
    }

    @CrossOrigin
    @PostMapping("/edit-profile")
    fun editProfile(@CookieValue("jwt") jwt: String?, @Valid @RequestBody editProfileDto: EditProfileDTO): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(MessageDTO("user/unauthorized", 401, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        }
        val jwtUser = userService.findById(UUID.fromString(jwtService.getJwtClaims(jwt).body.issuer))
        val jwtEmail = jwtUser.map(User::email).orElse("")

        // check if jwt token matches user
        if (editProfileDto.email != jwtEmail) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageDTO("user/jwt-email-mismatch", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        }
        if (userService.findByDisplayName(editProfileDto.displayName) != null) {
            return ResponseEntity.status(409).body(MessageDTO("user/displayname-conflict", 409, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        }
        val editedUser = userService.editProfile(editProfileDto)
        return ResponseEntity.ok(editedUser)

    }

    @CrossOrigin
    @PostMapping("/send-forgot-password-email")
    fun forgotPasswordEmail(@RequestBody email: String): ResponseEntity<Any> {
        val user = userService.findByEmail(email)
                ?: return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        val resetPasswordToken = ResetPasswordToken(user)
        resetPasswordTokenService.addResetPasswordToken(resetPasswordToken)
        val link = "https://theta-risk.com/reset-password?user="
        val msg = "<h1>Hello ${user.displayName}</h1><br><p>click this ${link}${email} to reset your password.</p><p>Token for development ${resetPasswordToken.resetPasswordToken}</p>"
        emailService.sendMail("no-reply@theta-risk.com", user.email, "Password reset", msg)
        return ResponseEntity.ok(MessageDTO("email/reset-sent!", 200, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
    }

    @CrossOrigin
    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody passwordDto: ResetPasswordDto): ResponseEntity<Any> {
        if (passwordDto.confirmNewPassword != passwordDto.newPassword)
            return ResponseEntity.badRequest().body(MessageDTO("user/password-mismatch", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))

        val resetPasswordToken = resetPasswordTokenService.findByConfirmationToken(passwordDto.resetPasswordToken)
        val user = userService.findByEmail(resetPasswordToken!!.userEntity!!.email)
        return if (user == null) {
            return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString()))
        } else {
            user.password = BCryptPasswordEncoder().encode(passwordDto.newPassword)
            userService.save(user)
            ResponseEntity.ok(user)
        }
    }


    /**
     * On logout just expire the cookie
     * */
}