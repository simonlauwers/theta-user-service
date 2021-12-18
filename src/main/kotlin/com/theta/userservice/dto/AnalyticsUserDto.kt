package com.theta.userservice.dto

import java.time.LocalDateTime
import java.util.*

class AnalyticsUserDto (
        val id: UUID,
        val lastLogin: LocalDateTime
)