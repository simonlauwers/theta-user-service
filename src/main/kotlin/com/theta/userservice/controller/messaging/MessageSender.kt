package com.theta.userservice.controller.messaging

import Sl4jLogger.Companion.log
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.theta.userservice.controller.dto.AnalyticsUserDto
import com.theta.userservice.controller.dto.GameUserDto
import lombok.extern.slf4j.Slf4j
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Slf4j
@Component
class MessageSender {
    private var rabbitTemplate: RabbitTemplate? = null
    private var properties: MessagingConfig? = null
    private val objectMapper: ObjectMapper = ObjectMapper()


    @Autowired
    fun messageSender(rabbitTemplate: RabbitTemplate, properties: MessagingConfig) {
        this.rabbitTemplate = rabbitTemplate
        this.properties = properties
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    fun sendUser(user: AnalyticsUserDto) {
        try {
            rabbitTemplate?.convertAndSend(properties?.routingKeyUser!!, objectMapper.writeValueAsString(user))
            log.info("Message user with id: " + user.id.toString() + " was sent to the user_logged_in queue");
        } catch (e: JsonProcessingException) {
            log.warn("Object mapper could not parse user with id: " + user.id.toString())
        }
    }

    fun sendUser(user: GameUserDto) {
        try {
            rabbitTemplate?.convertAndSend(properties?.routingKeyGameUser!!, objectMapper.writeValueAsString(user))
            log.info("Message user with id: " + user.uuid.toString() + " was sent to the game_user queue");
        } catch (e: JsonProcessingException){
            log.warn("Object mapper could not parse user with id: " + user.uuid.toString())
        }
    }
}