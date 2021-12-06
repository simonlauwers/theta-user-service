package com.theta.userservice.service

import com.theta.userservice.domain.exceptions.PasswordMismatchException
import com.theta.userservice.domain.model.ResetPasswordToken
import com.theta.userservice.domain.model.User
import com.theta.userservice.dto.ResetPasswordDto
import com.theta.userservice.repository.ResetPasswordTokenRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class ResetPasswordTokenService(val resetPasswordTokenRepository: ResetPasswordTokenRepository, val userService: UserService) {
    fun addResetPasswordToken(token: ResetPasswordToken): ResetPasswordToken {
        return resetPasswordTokenRepository.save(token)
    }

    fun findByConfirmationToken(token: String): ResetPasswordToken? {
        return resetPasswordTokenRepository.findByResetPasswordToken(token)
    }

    fun resetPassword(passwordDto: ResetPasswordDto): User {
        if (passwordDto.confirmNewPassword != passwordDto.newPassword)
            throw PasswordMismatchException("user/password-mismatch")

        val resetPasswordToken = findByConfirmationToken(passwordDto.resetPasswordToken)
        val user = userService.findByEmail(resetPasswordToken!!.userEntity!!.email)
                ?: throw EntityNotFoundException("user/not-found")
        user.password = BCryptPasswordEncoder().encode(passwordDto.newPassword)
        userService.save(user)
        return user
    }
}