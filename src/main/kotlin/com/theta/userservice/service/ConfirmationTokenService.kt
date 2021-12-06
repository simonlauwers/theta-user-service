package com.theta.userservice.service

import com.theta.userservice.domain.model.ConfirmationToken
import com.theta.userservice.domain.model.User
import com.theta.userservice.dto.TokenDto
import com.theta.userservice.repository.ConfirmationTokenRepository
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class ConfirmationTokenService(val confirmationTokenRepository: ConfirmationTokenRepository, val userService: UserService) {

    fun addConfirmationToken(token: ConfirmationToken): ConfirmationToken {
        return confirmationTokenRepository.save(token)
    }

    fun findByConfirmationToken(token: String) : ConfirmationToken?{
        return confirmationTokenRepository.findByConfirmationToken(token)
    }

    fun findByUserEmail(email:String) : ConfirmationToken?{
        return confirmationTokenRepository.findByUserEntityEmail(email)
    }

    fun confirmAccount(tokenDto: TokenDto): User {
        val confirmationToken = findByConfirmationToken(tokenDto.token)
                ?: throw EntityNotFoundException("confirmationtoken/not-found")
        val userToUpdate = userService.findById(confirmationToken.userEntity!!.userId)
        userToUpdate.get().isEnabled = true
        return userService.save(userToUpdate.get())
    }

}