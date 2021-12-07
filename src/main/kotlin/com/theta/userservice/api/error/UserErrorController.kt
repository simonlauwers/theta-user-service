package com.theta.userservice.api.error


import Sl4jLogger.Companion.log
import com.theta.userservice.api.converters.ResponseMessageDtoConverter
import com.theta.userservice.domain.exceptions.*
import com.theta.userservice.dto.ResponseMessageDto
import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpServerErrorException
import javax.persistence.EntityNotFoundException
import javax.validation.ConstraintViolationException

@ControllerAdvice
@Slf4j
class UserErrorController(val responseMessageDtoConverter: ResponseMessageDtoConverter) {

    @ExceptionHandler(UserDisplayNameConflict::class, UserEmailConflictException::class)
    fun conflict(e: Exception): ResponseEntity<ResponseMessageDto>{
        val responseMessageDto = responseMessageDtoConverter.convert(e, 409)
        log.error("Entity conflict: " + e.message)
        return ResponseEntity<ResponseMessageDto>(responseMessageDto, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ConstraintViolationException::class, UserIsBannedException::class, UserNotConfirmedException::class, InvalidPasswordException::class, JwtEmailMismatchException::class, PasswordMismatchException::class)
    fun badRequest(e : Exception) : ResponseEntity<ResponseMessageDto>{
        val responseMessageDto = responseMessageDtoConverter.convert(e, 400)
        log.error("Bad request: " + e.message)
        return ResponseEntity<ResponseMessageDto>(responseMessageDto, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(EntityNotFoundException::class)
    fun notFound(e: Exception) : ResponseEntity<ResponseMessageDto>{
        val responseMessageDto = responseMessageDtoConverter.convert(e, 404)
        log.error("Entity not found: " + e.message)
        return ResponseEntity<ResponseMessageDto>(responseMessageDto, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorized(e: Exception) : ResponseEntity<ResponseMessageDto> {
        val responseMessageDto = responseMessageDtoConverter.convert(e, 401)
        log.error("Unauthorized user exception: " + e.message)
        return ResponseEntity<ResponseMessageDto>(responseMessageDto, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError::class)
    fun internalServerError(e: Exception) : ResponseEntity<ResponseMessageDto> {
        val responseMessageDto = responseMessageDtoConverter.convert(e, 500)
        log.error("Internal server error occured: " + e.message)
        return ResponseEntity<ResponseMessageDto>(responseMessageDto, HttpStatus.UNAUTHORIZED)
    }
}