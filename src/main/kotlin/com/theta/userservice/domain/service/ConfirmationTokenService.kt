package com.theta.userservice.domain.service

import Sl4jLogger.Companion.log
import com.theta.userservice.controller.dto.GameUserDto
import com.theta.userservice.domain.model.ConfirmationToken
import com.theta.userservice.domain.model.User
import com.theta.userservice.controller.dto.TokenDto
import com.theta.userservice.controller.dto.UserDto
import com.theta.userservice.controller.messaging.MessageSender
import com.theta.userservice.repository.ConfirmationTokenRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
@Slf4j
class ConfirmationTokenService(val confirmationTokenRepository: ConfirmationTokenRepository, val userService: UserService, val messageSender: MessageSender) {

    fun addConfirmationToken(token: ConfirmationToken): ConfirmationToken {
        return confirmationTokenRepository.save(token)
    }

    fun findByConfirmationToken(token: String): ConfirmationToken? {
        return confirmationTokenRepository.findByConfirmationToken(token)
    }

    fun findByUserEmail(email: String): ConfirmationToken? {
        return confirmationTokenRepository.findByUserEntityEmail(email)
    }

    fun confirmAccount(tokenDto: TokenDto): UserDto {
        val confirmationToken = findByConfirmationToken(tokenDto.token)
                ?: throw EntityNotFoundException("confirmationtoken/not-found")
        val userToUpdate = userService.findById(confirmationToken.userEntity!!.userId)
        userToUpdate.get().isEnabled = true
        log.info("user: " + userToUpdate.get().email + " has been confirmed!")
        messageSender.sendUser(GameUserDto(userToUpdate.get().userId))
        return userService.update(userToUpdate.get())
    }
}