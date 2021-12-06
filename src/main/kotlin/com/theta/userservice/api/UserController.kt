package com.theta.userservice.api

import com.theta.userservice.domain.model.User
import com.theta.userservice.dto.*
import com.theta.userservice.service.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
class UserController(val userService: UserService, val jwtService: JwtService, val emailService: EmailService, val confirmationTokenService: ConfirmationTokenService, val resetPasswordTokenService: ResetPasswordTokenService) {
    @CrossOrigin
    @PostMapping("/register")
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<User> {
        return ResponseEntity(userService.registerUser(registerDto), HttpStatus.CREATED)
    }

    @CrossOrigin
    @PostMapping("/confirm-account")
    fun confirmAccount(@RequestBody tokenDTO: TokenDto): ResponseEntity<User> {
        return ResponseEntity(confirmationTokenService.confirmAccount(tokenDTO), HttpStatus.ACCEPTED)
    }

    @CrossOrigin
    @PostMapping("/send-confirmation-email")
    fun sendConfirmationEmail(@RequestBody emailDTO: EmailDto): ResponseEntity<ResponseMessageDto> {
        return ResponseEntity(emailService.sendConfirmationEmail(emailDTO), HttpStatus.ACCEPTED)
    }

    @CrossOrigin
    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto, response: HttpServletResponse): ResponseEntity<User> {
        return ResponseEntity(userService.login(loginDto, response), HttpStatus.ACCEPTED)
    }

    @CrossOrigin
    @GetMapping("/whoami")
    fun test(@CookieValue("jwt") jwt: String?): ResponseEntity<User> {
        return ResponseEntity(jwtService.whoAmI(jwt), HttpStatus.ACCEPTED)
    }

    @CrossOrigin
    @PostMapping("/edit-profile")
    fun editProfile(@CookieValue("jwt") jwt: String?, @Valid @RequestBody editProfileDto: EditProfileDto): ResponseEntity<User> {
        return ResponseEntity(userService.editProfile(editProfileDto, jwt), HttpStatus.ACCEPTED)
    }

    @CrossOrigin
    @PostMapping("/send-forgot-password-email")
    fun forgotPasswordEmail(@RequestBody emailDTO: EmailDto): ResponseEntity<ResponseMessageDto> {
        return ResponseEntity(emailService.sendForgotPasswordEmail(emailDTO), HttpStatus.ACCEPTED)

    }

    @CrossOrigin
    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody passwordDto: ResetPasswordDto): ResponseEntity<User> {
        return ResponseEntity(resetPasswordTokenService.resetPassword(passwordDto), HttpStatus.ACCEPTED)
    }

    /**
     * To delete a cookie we need to create a cookie with the same name
     * as the cookie we want to delete. We also need to set the max age of that newly created
     * cookie to 0 and then add it to the Servlet's response method
     */
    @CrossOrigin
    @PostMapping("/logout")
    fun logout(@CookieValue jwt: Cookie, response: HttpServletResponse) {
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        response.addCookie(cookie)
    }


}