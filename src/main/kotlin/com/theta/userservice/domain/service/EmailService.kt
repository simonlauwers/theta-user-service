package com.theta.userservice.domain.service

import Sl4jLogger.Companion.log
import com.theta.userservice.domain.model.ConfirmationToken
import com.theta.userservice.domain.model.ResetPasswordToken
import com.theta.userservice.controller.dto.EmailDto
import com.theta.userservice.controller.dto.ResponseMessageDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.mail.internet.MimeMessage
import javax.persistence.EntityNotFoundException


@Service
class EmailService(val mailSender: JavaMailSender, val userService: UserService, val confirmationTokenService: ConfirmationTokenService, val resetPasswordTokenService: ResetPasswordTokenService) {
    @Value("\${webclient-baseurl}")
    private val baseUrl: String = ""
    @Async
    fun sendMail(from: String, to: String, subject: String, msg: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        message.subject = subject
        val helper = MimeMessageHelper(message, true)
        helper.setFrom(from)
        helper.setTo(to)
        helper.setText(msg, true)
        mailSender.send(message)
    }

    fun sendConfirmationEmail(emailDto: EmailDto): ResponseMessageDto {
        val user = userService.findByEmail(emailDto.email)
                ?: throw EntityNotFoundException("user/not-found")
        val confirmationToken = ConfirmationToken(user)
        confirmationTokenService.addConfirmationToken(confirmationToken)
        val msg = "<h1>Hello, ${user.displayName}!</h1><br><p>Confirm your account in the next 24hr using this <a href=\"${baseUrl}${confirmationToken.confirmationToken}/confirm\">link</a></p><p>Token for development testing: ${confirmationToken.confirmationToken}"
        sendMail("no-reply@theta-risk.com", emailDto.email, "Confirm your account!", msg)
        log.info("confirmation email sent to " + user.email)
        return ResponseMessageDto.Builder().message("email/confirmation-sent").status(200).timeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build()
    }

    fun sendForgotPasswordEmail(emailDto: EmailDto) : ResponseMessageDto {
        val user = userService.findByEmail(emailDto.email)
                ?: throw EntityNotFoundException("user/not-found")
        val resetPasswordToken = ResetPasswordToken(user)
        resetPasswordTokenService.addResetPasswordToken(resetPasswordToken)
        val msg = "<h1>Hello ${user.displayName}</h1><br><p>click this <a href=\"${baseUrl}${resetPasswordToken.resetPasswordToken}/reset\">link</a> ${emailDto.email} to reset your password.</p><p>Token for development ${resetPasswordToken.resetPasswordToken}</p>"
        sendMail("no-reply@theta-risk.com", user.email, "Password reset", msg)
        log.info("forgot password sent to " + user.email)
        return ResponseMessageDto.Builder().message("email/reset-sent").status(200).timeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build()
    }

}