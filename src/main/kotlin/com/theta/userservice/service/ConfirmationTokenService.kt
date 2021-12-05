package com.theta.userservice.service

import com.theta.userservice.model.ConfirmationToken
import com.theta.userservice.repository.ConfirmationTokenRepository
import org.springframework.stereotype.Service

@Service
class ConfirmationTokenService(val confirmationTokenRepository: ConfirmationTokenRepository) {

    fun addConfirmationToken(token: ConfirmationToken): ConfirmationToken {
        return confirmationTokenRepository.save(token)
    }

    fun findByConfirmationToken(token: String) : ConfirmationToken?{
        return confirmationTokenRepository.findByConfirmationToken(token)
    }
}