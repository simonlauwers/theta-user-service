package com.theta.userservice

//import org.hamcrest.Matchers.notNullValue

import com.google.gson.Gson
import com.theta.userservice.dto.RegisterDTO
import com.theta.userservice.model.ConfirmationToken
import com.theta.userservice.model.User
import com.theta.userservice.service.ConfirmationTokenService
import com.theta.userservice.service.JwtService
import com.theta.userservice.service.ResetPasswordTokenService
import com.theta.userservice.service.UserService
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.LogConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.matcher.ResponseAwareMatcher
import io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.apache.http.HttpStatus
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTests  @Autowired constructor(val userService: UserService, val confirmationTokenService: ConfirmationTokenService, val jwtService: JwtService, val resetPasswordTokenService: ResetPasswordTokenService) {
    private val gson = Gson()
    companion object {
        lateinit var requestSpecification: RequestSpecification
    }

    @BeforeAll
    fun setup(){
        val logConfig = LogConfig.logConfig()
                .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)
        val config = RestAssuredConfig.config().logConfig(logConfig)

        requestSpecification = RequestSpecBuilder()
                .setBaseUri("http://localhost:8080")
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .setRelaxedHTTPSValidation()
                .setConfig(config)
                .build()

    }

    @AfterAll
    fun tearDown(){
        resetPasswordTokenService.deleteAll()
        confirmationTokenService.deleteAll()
        userService.deleteAll()
        RestAssured.reset()
    }



    @Test
    @Order(1)
    fun `register a single user`() {
        val reqBody = gson.toJson(RegisterDTO("simonlauw", "simon.lauwers@telenet.be", "Badeendjes1972"))
        Given {
            spec(requestSpecification).body(reqBody)
        } When {
            post("/register")
        } Then {
            assertThat().body(matchesJsonSchemaInClasspath("user-schema.json")).log()
            assertThat().body("isEnabled") { equalTo(false) }
            statusCode(HttpStatus.SC_OK)
        }
    }

    @Test
    @Order(2)
    fun `send confirmation email`() {
        Given {
            spec(requestSpecification).body("simon.lauwers@telenet.be")
        } When {
            post("/send-confirmation-email")
        } Then {
            assertThat().body(matchesJsonSchemaInClasspath("message-schema.json")).log()
            assertThat().body("message") { equalTo("Confirmation email sent!") }
            statusCode(HttpStatus.SC_OK)
        }
    }

}
