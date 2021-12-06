package com.theta.userservice.api.error


import com.theta.userservice.api.converters.ResponseMessageDtoConverter
import com.theta.userservice.domain.exceptions.*
import com.theta.userservice.dto.ResponseMessageDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.persistence.EntityNotFoundException
import javax.validation.ConstraintViolationException

@ControllerAdvice
class UserErrorController(val responseMessageDtoConverter: ResponseMessageDtoConverter) {

    @ExceptionHandler(UserDisplayNameConflict::class, UserEmailConflictException::class)
    fun conflict(e: Exception): ResponseEntity<ResponseMessageDto>{
        val responseMessageDto = responseMessageDtoConverter.convert(e, 409)
        return ResponseEntity<ResponseMessageDto>(responseMessageDto, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ConstraintViolationException::class, UserIsBannedException::class, UserNotConfirmedException::class, InvalidPasswordException::class)
    fun badRequest(e : Exception) : ResponseEntity<ResponseMessageDto>{
        val responseMessageDto = responseMessageDtoConverter.convert(e, 400)
        return ResponseEntity<ResponseMessageDto>(responseMessageDto, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(EntityNotFoundException::class)
    fun notFound(e: Exception) : ResponseEntity<ResponseMessageDto>{
        val responseMessageDto = responseMessageDtoConverter.convert(e, 404)
        return ResponseEntity<ResponseMessageDto>(responseMessageDto, HttpStatus.NOT_FOUND)
    }
}