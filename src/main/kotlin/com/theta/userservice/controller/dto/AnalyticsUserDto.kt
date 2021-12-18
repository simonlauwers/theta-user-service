package com.theta.userservice.controller.dto

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class AnalyticsUserDto (
        val id: UUID,
        val lastLogin: LocalDateTime
)