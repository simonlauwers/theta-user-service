package com.theta.userservice.controller.converters

import com.theta.userservice.controller.dto.ResponseMessageDto
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class ResponseMessageDtoConverter {
    fun convert(e: Exception, statusCode: Int): ResponseMessageDto {
        return ResponseMessageDto.Builder()
                .message(e.message!!)
                .status(statusCode)
                .timeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build()
    }
}