package com.theta.userservice.service

import com.theta.userservice.model.ConfirmationToken
import com.theta.userservice.model.ResetPasswordToken
import com.theta.userservice.repository.ResetPasswordTokenRepository

import org.springframework.stereotype.Service

@Service
class ResetPasswordTokenService(val resetPasswordTokenRepository: ResetPasswordTokenRepository) {
    fun addResetPasswordToken(token: ResetPasswordToken): ResetPasswordToken {
        return resetPasswordTokenRepository.save(token)
    }

    fun findByConfirmationToken(token: String): ResetPasswordToken? {
        return resetPasswordTokenRepository.findByResetPasswordToken(token)
    }

    /* FOR TESTING PURPOSES ONLY!!! */
    fun deleteAll(){
        return resetPasswordTokenRepository.deleteAll();
    }
}