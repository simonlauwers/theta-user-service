package com.theta.userservice.service

import Sl4jLogger
import Sl4jLogger.Companion.log
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*
import javax.mail.MessagingException
import javax.mail.internet.MimeMessage


@Sl4jLogger
@Service
class EmailService(val mailSender: JavaMailSender) {
    @Async
    fun sendMail(from: String, to: String, subject: String, msg: String) {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            message.subject = subject
            val helper = MimeMessageHelper(message, true)
            helper.setFrom(from)
            helper.setTo(to)
            helper.setText(msg, true)
            mailSender.send(message)
        } catch (ex: MessagingException) {
            log.error("Sending email failed! Exception: ", ex)
        }
    }
}