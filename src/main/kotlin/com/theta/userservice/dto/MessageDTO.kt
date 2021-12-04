package com.theta.userservice.dto

import org.joda.time.DateTime
import org.springframework.http.HttpStatus
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

class MessageDTO(
        val message: String,
        val status: Int,
        val timeStamp: String
)