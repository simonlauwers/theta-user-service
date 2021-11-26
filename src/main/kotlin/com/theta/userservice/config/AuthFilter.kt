package com.theta.userservice.config


import com.theta.userservice.service.JwtService
import io.jsonwebtoken.JwtException
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AuthFilter(val jwtService: JwtService) : HandlerInterceptor {

    var log = LoggerFactory.getLogger(this.javaClass)!!

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        return true
        return try {
            val authorizationHeader = request.getHeader("Authorization")
                    ?: throw JwtException("Authorization header should not be null")

            // throws exceptions if the jwt is not valid
            jwtService.getJwtClaims(authorizationHeader)
            true
        } catch (e: JwtException) {
            log.warn(e.message)
            response.status = HttpStatus.BAD_REQUEST.value()
            false
        }
    }

}