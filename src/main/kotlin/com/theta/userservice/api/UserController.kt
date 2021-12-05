package com.theta.userservice.api

import Sl4jLogger
import Sl4jLogger.Companion.log
import com.theta.userservice.dto.*
import com.theta.userservice.model.ConfirmationToken
import com.theta.userservice.model.ResetPasswordToken
import com.theta.userservice.model.User
import com.theta.userservice.service.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@Sl4jLogger
class UserController(val userService: UserService, val jwtService: JwtService, val emailService: EmailService, val confirmationTokenService: ConfirmationTokenService, val resetPasswordTokenService: ResetPasswordTokenService, val roleService: RoleService) {
    @CrossOrigin
    @PostMapping("/register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<Any> {
        try {
            val user = User()
            user.displayName = body.displayName
            user.email = body.email
            user.password = BCryptPasswordEncoder().encode(body.password)
            user.role = roleService.findByName("user")
            user.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

            return if (userService.findByEmail(user.email) != null)
                ResponseEntity.status(409).body(MessageDTO("user/email-conflict", 409, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
            else if (userService.findByDisplayName(user.displayName) != null) {
                ResponseEntity.status(409).body(MessageDTO("user/display-name-conflict", 409, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
            } else {
                val newUser = userService.save(user)
                return ResponseEntity.ok(newUser)
            }
        } catch (e: Exception) {
            return ResponseEntity.status(400).body(MessageDTO("user/malformed-body", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        }
    }

    @CrossOrigin
    @PostMapping("/confirm-account")
    fun confirmAccount(@RequestBody tokenDTO: TokenDTO): ResponseEntity<Any> {
        val confirmationToken = confirmationTokenService.findByConfirmationToken(tokenDTO.token)
                ?: return ResponseEntity.badRequest().body(MessageDTO("confirmationtoken/not-found", 200, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));

        val userToUpdate = userService.findById(confirmationToken.userEntity!!.userId)
        userToUpdate.get().isEnabled = true;
        val updatedUser = userService.save(userToUpdate.get())
        return ResponseEntity.ok(updatedUser)

    }

    @CrossOrigin
    @PostMapping("/send-confirmation-email")
    fun sendConfirmationEmail(@RequestBody emailDTO: EmailDTO): ResponseEntity<Any> {
        try {
            val user = userService.findByEmail(emailDTO.email)
                    ?: return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
            val confirmationToken = ConfirmationToken(user)
            confirmationTokenService.addConfirmationToken(confirmationToken)
            val link = "https://theta-risk.com/game/confirm?token="
            val msg = "<h1>Hello, ${user.displayName}!</h1><br><p>Confirm your account in the next 24hr using this <a href=\"${link}${confirmationToken.confirmationToken}\">link</a></p><p>Token for development testing: ${confirmationToken.confirmationToken}"
            emailService.sendMail("no-reply@theta-risk.com", emailDTO.email, "Confirm your account!", msg)

            return ResponseEntity.status(200).body(MessageDTO("email/confirmation-sent", 200, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        } catch (e: Exception) {
            return ResponseEntity.status(400).body(MessageDTO("user/invalid-email", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        }
    }

    @CrossOrigin
    @PostMapping("/login")
    fun login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        val user = userService.findByEmail(body.email)
                ?: return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        if (!user.isEnabled)
            return ResponseEntity.badRequest().body(MessageDTO("user/not-confirmed", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        val responseHeaders = HttpHeaders()
        return if (!BCryptPasswordEncoder().matches(body.password, user.password))
            ResponseEntity.badRequest().body(MessageDTO("user/invalid-password", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        else {
            user.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            userService.save(user)
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
                return ResponseEntity.status(401).body(MessageDTO("user/unauthorized", 401, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
            }
            val body = jwtService.getJwtClaims(jwt).body
            log.info(body.issuer)

            val user = userService.findById(UUID.fromString(body.issuer))

            return if (user.isPresent) {
                ResponseEntity.ok(user)
            } else {
                return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
            }
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(MessageDTO("user/unauthorized", 401, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        }
    }

    @CrossOrigin
    @PostMapping("/edit-profile")
    fun editProfile(@CookieValue("jwt") jwt: String?, @Valid @RequestBody editProfileDto: EditProfileDTO): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(MessageDTO("user/unauthorized", 401, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        }
        val jwtUser = userService.findById(UUID.fromString(jwtService.getJwtClaims(jwt).body.issuer))
        val jwtEmail = jwtUser.map(User::email).orElse("")

        // check if jwt token matches user
        if (editProfileDto.email != jwtEmail) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageDTO("user/jwt-email-mismatch", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        }
        if (userService.findByDisplayName(editProfileDto.displayName) != null) {
            return ResponseEntity.status(409).body(MessageDTO("user/displayname-conflict", 409, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        }
        val editedUser = userService.editProfile(editProfileDto)
        return ResponseEntity.ok(editedUser)

    }

    @CrossOrigin
    @PostMapping("/send-forgot-password-email")
    fun forgotPasswordEmail(@RequestBody emailDTO: EmailDTO): ResponseEntity<Any> {
        val user = userService.findByEmail(emailDTO.email)
                ?: return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        val resetPasswordToken = ResetPasswordToken(user)
        resetPasswordTokenService.addResetPasswordToken(resetPasswordToken)
        val link = "https://theta-risk.com/reset-password?user="
        val msg = "<h1>Hello ${user.displayName}</h1><br><p>click this <a href=\"${link}${resetPasswordToken.tokenId}\">link</a> ${emailDTO.email} to reset your password.</p><p>Token for development ${resetPasswordToken.resetPasswordToken}</p>"
        emailService.sendMail("no-reply@theta-risk.com", user.email, "Password reset", msg)
        return ResponseEntity.ok(MessageDTO("email/reset-sent!", 200, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
    }

    @CrossOrigin
    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody passwordDto: ResetPasswordDTO): ResponseEntity<Any> {
        if (passwordDto.confirmNewPassword != passwordDto.newPassword)
            return ResponseEntity.badRequest().body(MessageDTO("user/password-mismatch", 400, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))

        val resetPasswordToken = resetPasswordTokenService.findByConfirmationToken(passwordDto.resetPasswordToken)
        val user = userService.findByEmail(resetPasswordToken!!.userEntity!!.email)
        return if (user == null) {
            return ResponseEntity.status(404).body(MessageDTO("user/not-found", 404, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
        } else {
            user.password = BCryptPasswordEncoder().encode(passwordDto.newPassword)
            userService.save(user)
            ResponseEntity.ok(user)
        }
    }

    /**
    * To delete a cookie we need to create a cookie with the same name
    * as the cookie we want to delete. We also need to set the max age of that newly created
    * cookie to 0 and then add it to the Servlet's response method
    */
    @CrossOrigin
    @PostMapping("/logout")
    fun logout(@CookieValue jwt: Cookie,  response: HttpServletResponse){
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        response.addCookie(cookie)
    }


}