package com.theta.userservice.api.controllers.messaging

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "messaging")
@NoArgsConstructor
@Getter
@Setter
class MessagingConfig {
    var routingKeyUser: String = ""
    var routingKeyGameUser: String = ""
}