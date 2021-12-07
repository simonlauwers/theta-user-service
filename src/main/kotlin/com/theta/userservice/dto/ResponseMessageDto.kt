package com.theta.userservice.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor

@Builder
@AllArgsConstructor
@NoArgsConstructor
class ResponseMessageDto private constructor(
        val message: String?,
        val status: Int?,
        val timeStamp: String?
)
{
    data class Builder(
            var message: String? = null,
            var status:Int? = null,
            var timeStamp: String? = null
    ){
        fun message(message: String) = apply { this.message = message }
        fun status(status: Int) = apply { this.status = status }
        fun timeStamp(timeStamp: String) = apply { this.timeStamp = timeStamp }
        fun build() = ResponseMessageDto(message, status, timeStamp)
    }
}