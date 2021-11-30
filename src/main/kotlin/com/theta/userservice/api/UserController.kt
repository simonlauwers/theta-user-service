package com.theta.userservice.api

import Sl4jLogger
import Sl4jLogger.Companion.log
import com.theta.userservice.dto.*
import com.theta.userservice.model.ConfirmationToken
import com.theta.userservice.model.ResetPasswordToken
import com.theta.userservice.model.User
import com.theta.userservice.service.*
import org.apache.coyote.Response
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.SimpleMailMessage
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@RequestMapping("/api")
@Sl4jLogger
class UserController(val userService: UserService, val jwtService: JwtService, val emailService: EmailService, val confirmationTokenService: ConfirmationTokenService, val resetPasswordTokenService: ResetPasswordTokenService) {
    @PostMapping("/register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<Any> {
        try {
            val user = User()
            user.displayName = body.displayName
            user.email = body.email
            user.password = BCryptPasswordEncoder().encode(body.password)

            return if (userService.findByEmail(user.email) != null)
                ResponseEntity("User already exists", HttpStatus.CONFLICT)
            else {
                val newUser = userService.save(user)
                val confirmationToken = ConfirmationToken(newUser)
                confirmationTokenService.addConfirmationToken(confirmationToken)
                val mailMessage = SimpleMailMessage()
                val link = "https://theta-risk.com/confirm?token=" + confirmationToken.confirmationToken
                mailMessage.setTo(newUser.email)
                mailMessage.setSubject("Confirm your email address!")
                mailMessage.setFrom("no-reply@theta-risk.com")
                mailMessage.setText("Hello " +  newUser.displayName + ", click this link to confirm your account: " + link + "(you can ignore this tokenId: " + confirmationToken.confirmationToken + ")")

                emailService.sendEmail(mailMessage)

                return ResponseEntity.ok(newUser)
            }
        }catch (e: Exception){
            return ResponseEntity.badRequest().body(MessageDTO(e.message.toString()))
        }
    }

    @PostMapping("/confirm-account")
    fun confirmAccount(@RequestBody token: String) : ResponseEntity<Any>{
        val confirmationToken = confirmationTokenService.findByConfirmationToken(token);

        if(confirmationToken != null){
           val userToUpdate = userService.findById(confirmationToken.userEntity!!.userId)
            userToUpdate.get().isEnabled = true;
            val updatedUser = userService.save(userToUpdate.get())
            return ResponseEntity.ok(updatedUser)
        }
        return ResponseEntity.badRequest().body(MessageDTO("Not matching confirmationtoken found in database!"))
    }

    @PostMapping("/login")
    fun login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        val user = userService.findByEmail(body.email)
                ?: return ResponseEntity.badRequest().body(MessageDTO("User not found!"))
        if(!user.isEnabled)
            return ResponseEntity.badRequest().body(MessageDTO("User is not yet confirmed! Check your email: + " + user.email))
        val responseHeaders = HttpHeaders()
        return if (!BCryptPasswordEncoder().matches(body.password, user.password))
            ResponseEntity.badRequest().body(MessageDTO("Invalid password!"))
        else {
            val jwt = jwtService.create(user)
            val cookie = Cookie("jwt", jwt)
            cookie.isHttpOnly = true
            response.addCookie(cookie)
            ResponseEntity.ok(MessageDTO("Success! Jwt cookie created!"))
        }
    }

    @GetMapping("/test")
    fun test(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body(MessageDTO("unauthenticated"))
            }
            val body = jwtService.getJwtClaims(jwt).body
            log.info(body.issuer)

            val user = userService.findById(UUID.fromString(body.issuer))

            return if (user.isPresent) {
                ResponseEntity.ok(user)
            } else {
                ResponseEntity.badRequest().body(MessageDTO("Couldn't find a user from that token"))
            }
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(MessageDTO("unauthenticated"))
        }
    }

    @PostMapping("/edit-profile")
    fun editProfile(@CookieValue("jwt") jwt: String?, @Valid @RequestBody editProfileDto: EditProfileDTO): ResponseEntity<Any> {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body(MessageDTO("unauthenticated"))
            }
            val jwtUser = userService.findById(UUID.fromString(jwtService.getJwtClaims(jwt).body.issuer))
            val jwtEmail = jwtUser.map(User::email).orElse("")

            // check if jwt token matches user
            if (editProfileDto.email != jwtEmail) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageDTO("Mismatch between jwt and user sent"))
            }
            val editedUser = userService.editProfile(editProfileDto)
            return ResponseEntity.ok(editedUser)
        } catch (e: DataIntegrityViolationException) {
            return ResponseEntity.badRequest().body(MessageDTO("This displayname already exists!"))
        }
    }

    @PostMapping("/send-forgot-password-email")
    fun forgotPasswordEmail(@RequestBody email: String) : ResponseEntity<Any>{
        val user = userService.findByEmail(email)
                ?: return ResponseEntity.badRequest().body(MessageDTO("No user found with the supplied email!"))
        val resetPasswordToken = ResetPasswordToken(user)
        resetPasswordTokenService.addResetPasswordToken(resetPasswordToken)
        val mailMessage = SimpleMailMessage()
        mailMessage.setTo(user.email)
        mailMessage.setSubject("Reset password")
        mailMessage.setFrom("no-reply@theta-risk.com")
        mailMessage.setText("Hello " +  user.displayName + ", click this link to reset your password:  https://theta-risk.com/reset-password?user=simon.lauwers@telenet.be (you can ignore this tokenId " + resetPasswordToken.resetPasswordToken + ")")
        emailService.sendEmail(mailMessage)
        return ResponseEntity.ok(MessageDTO("Reset email sent!"))
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody passwordDto: ResetPasswordDto) : ResponseEntity<Any> {
        if(passwordDto.confirmNewPassword != passwordDto.confirmNewPassword)
            return ResponseEntity.badRequest().body(MessageDTO("Passwords dont match!"))

        val resetPasswordToken = resetPasswordTokenService.findByConfirmationToken(passwordDto.resetPasswordToken)
        val user = userService.findByEmail(resetPasswordToken!!.userEntity!!.email)
        return if(user == null){
            ResponseEntity.badRequest().body(MessageDTO("Couldnt find a user with the specified email!"))
        }else{
            user.password = BCryptPasswordEncoder().encode(passwordDto.newPassword)
            userService.save(user)
            ResponseEntity.ok(user)
        }
    }

    /**
     * On logout just expire the token on client side
     * This doesnt require a endpoint on the server.
     * */
}