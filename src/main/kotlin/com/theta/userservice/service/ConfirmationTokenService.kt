package com.theta.userservice.service

import Sl4jLogger.Companion.log
import com.theta.userservice.domain.model.ConfirmationToken
import com.theta.userservice.domain.model.User
import com.theta.userservice.dto.TokenDto
import com.theta.userservice.repository.ConfirmationTokenRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
@Slf4j
class ConfirmationTokenService(val confirmationTokenRepository: ConfirmationTokenRepository, val userService: UserService) {

    fun addConfirmationToken(token: ConfirmationToken): ConfirmationToken {
        return confirmationTokenRepository.save(token)
    }

    fun findByConfirmationToken(token: String): ConfirmationToken? {
        return confirmationTokenRepository.findByConfirmationToken(token)
    }

    fun findByUserEmail(email: String): ConfirmationToken? {
        return confirmationTokenRepository.findByUserEntityEmail(email)
    }

    fun confirmAccount(tokenDto: TokenDto): User {
        val confirmationToken = findByConfirmationToken(tokenDto.token)
                ?: throw EntityNotFoundException("confirmationtoken/not-found")
        val userToUpdate = userService.findById(confirmationToken.userEntity!!.userId)
        userToUpdate.get().isEnabled = true
        log.info("user: " + userToUpdate.get().email + " has been confirmed!")
        return userService.update(userToUpdate.get())
    }
}