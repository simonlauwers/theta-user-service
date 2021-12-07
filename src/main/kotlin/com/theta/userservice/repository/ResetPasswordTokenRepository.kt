package com.theta.userservice.repository

import com.theta.userservice.domain.model.ResetPasswordToken
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ResetPasswordTokenRepository : CrudRepository<ResetPasswordToken, UUID> {
    fun findByResetPasswordToken(confirmationToken: String) : ResetPasswordToken?
}