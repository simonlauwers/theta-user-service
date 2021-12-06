package com.theta.userservice.repository


import com.theta.userservice.domain.model.ConfirmationToken
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ConfirmationTokenRepository: CrudRepository<ConfirmationToken, UUID> {
    fun findByConfirmationToken(confirmationToken: String) : ConfirmationToken?
    fun findByUserEntityEmail(email: String) : ConfirmationToken?
}