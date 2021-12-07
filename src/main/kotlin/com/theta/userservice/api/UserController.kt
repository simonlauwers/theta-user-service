package com.theta.userservice.api

import com.theta.userservice.domain.model.User
import com.theta.userservice.dto.*
import com.theta.userservice.service.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
class UserController(val userService: UserService, val emailService: EmailService, val confirmationTokenService: ConfirmationTokenService, val resetPasswordTokenService: ResetPasswordTokenService) {
    @PostMapping("/register")
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<User> {
        return ResponseEntity(userService.registerUser(registerDto), HttpStatus.CREATED)
    }

    @PostMapping("/confirm-account")
    fun confirmAccount(@RequestBody tokenDTO: TokenDto): ResponseEntity<User> {
        return ResponseEntity(confirmationTokenService.confirmAccount(tokenDTO), HttpStatus.ACCEPTED)
    }

    @PostMapping("/send-confirmation-email")
    fun sendConfirmationEmail(@RequestBody emailDTO: EmailDto): ResponseEntity<ResponseMessageDto> {
        return ResponseEntity(emailService.sendConfirmationEmail(emailDTO), HttpStatus.ACCEPTED)
    }

    @CrossOrigin
    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto, response: HttpServletResponse): ResponseEntity<User> {
        return ResponseEntity(userService.login(loginDto, response), HttpStatus.ACCEPTED)
    }

    @GetMapping("/whoami")
    fun test(@CookieValue("jwt") jwt: String?): ResponseEntity<User> {
        return ResponseEntity(userService.whoAmI(jwt), HttpStatus.ACCEPTED)
    }

    @PostMapping("/edit-profile")
    fun editProfile(@CookieValue("jwt") jwt: String?, @Valid @RequestBody editProfileDto: EditProfileDto): ResponseEntity<User> {
        return ResponseEntity(userService.editProfile(editProfileDto, jwt), HttpStatus.ACCEPTED)
    }

    @PostMapping("/send-forgot-password-email")
    fun forgotPasswordEmail(@RequestBody emailDTO: EmailDto): ResponseEntity<ResponseMessageDto> {
        return ResponseEntity(emailService.sendForgotPasswordEmail(emailDTO), HttpStatus.ACCEPTED)

    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody passwordDto: ResetPasswordDto): ResponseEntity<User> {
        return ResponseEntity(resetPasswordTokenService.resetPassword(passwordDto), HttpStatus.ACCEPTED)
    }

    /**
     * To delete the previous jwt cookie we need to create a cookie with the same name
     * and set the value to an emtpy string and the maxAge to 0.
     */
    @PostMapping("/logout")
    fun logout(@CookieValue jwt: Cookie, response: HttpServletResponse) : ResponseEntity<ResponseMessageDto> {
        val cookie = Cookie("jwt", "")
        cookie.isHttpOnly = true
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity(
                ResponseMessageDto.Builder()
                        .message("user/logged-out")
                        .status(200)
                        .timeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .build(), HttpStatus.ACCEPTED)
    }


}