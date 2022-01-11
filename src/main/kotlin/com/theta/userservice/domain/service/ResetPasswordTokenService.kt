package com.theta.userservice.domain.service

import Sl4jLogger.Companion.log
import com.theta.userservice.domain.exceptions.PasswordMismatchException
import com.theta.userservice.domain.model.ResetPasswordToken
import com.theta.userservice.domain.model.User
import com.theta.userservice.controller.dto.ResetPasswordDto
import com.theta.userservice.controller.dto.UserDto
import com.theta.userservice.repository.ResetPasswordTokenRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
@Slf4j
class ResetPasswordTokenService(val resetPasswordTokenRepository: ResetPasswordTokenRepository, val userService: UserService) {
    fun addResetPasswordToken(token: ResetPasswordToken): ResetPasswordToken {
        return resetPasswordTokenRepository.save(token)
    }

    fun findByConfirmationToken(token: String): ResetPasswordToken? {
        return resetPasswordTokenRepository.findByResetPasswordToken(token)
    }

    fun resetPassword(passwordDto: ResetPasswordDto): UserDto {
        if (passwordDto.confirmNewPassword != passwordDto.newPassword)
            throw PasswordMismatchException("user/password-mismatch")

        val resetPasswordToken = findByConfirmationToken(passwordDto.resetPasswordToken)
        val user = userService.findByEmail(resetPasswordToken!!.userEntity!!.email)
                ?: throw EntityNotFoundException("user/not-found")
        user.password = BCryptPasswordEncoder().encode(passwordDto.newPassword)
        userService.save(user)
        log.info("user " + user.email + " password has been reset!")
        return UserDto(user.userId, user.email, user.displayName, user.isEnabled, user.isBanned, user.provider, user.role!!, user.profilePicture, user.lastLogin)
    }
}