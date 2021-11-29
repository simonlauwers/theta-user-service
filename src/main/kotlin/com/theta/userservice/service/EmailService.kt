package com.theta.userservice.service

import com.theta.userservice.model.ConfirmationToken
import com.theta.userservice.model.User
import com.theta.userservice.repository.ConfirmationTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*


@Service
class EmailService(val javaMailSender: JavaMailSender, val confirmationTokenRepository: ConfirmationTokenRepository) {
    @Async
    fun sendEmail(email: SimpleMailMessage?) {
        javaMailSender.send(email)
    }

    fun addConfirmationToken(token: ConfirmationToken): ConfirmationToken {
        return confirmationTokenRepository.save(token)
    }

    fun findByConfirmationToken(token: String) : ConfirmationToken?{
        return confirmationTokenRepository.findByConfirmationToken(token)
    }
}