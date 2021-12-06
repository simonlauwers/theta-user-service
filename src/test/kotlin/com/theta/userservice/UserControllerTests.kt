package com.theta.userservice

//import org.hamcrest.Matchers.notNullValue

import com.google.gson.Gson
import com.theta.userservice.dto.EmailDto
import com.theta.userservice.dto.LoginDto
import com.theta.userservice.dto.RegisterDto
import com.theta.userservice.dto.TokenDto
import com.theta.userservice.domain.model.ConfirmationToken
import com.theta.userservice.domain.model.User
import com.theta.userservice.service.*
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.LogConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.apache.http.HttpStatus
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import javax.transaction.Transactional


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTests @Autowired constructor(val userService: UserService, val roleService: RoleService, val confirmationTokenService: ConfirmationTokenService) {
    private val gson = Gson()

    companion object {
        lateinit var requestSpecification: RequestSpecification
    }

    @BeforeAll
    fun setup() {
        val logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)
        val config = RestAssuredConfig.config().logConfig(logConfig)

        requestSpecification = RequestSpecBuilder().setBaseUri("http://localhost:8080").setContentType(ContentType.JSON).setRelaxedHTTPSValidation().setConfig(config).build()
        // test data
        val simon = userService.save(User("simon.lauwers4@gmail.com", "Clubvantstad01", "RadjaFanAccount", false, true, roleService.findByName("user")!!))
        val confirmationToken = ConfirmationToken(simon)
        confirmationTokenService.addConfirmationToken(confirmationToken)

    }

    @AfterAll
    fun tearDown() {
        userService.deleteUser(userService.findByEmail("simon.lauwers@telenet.be")!!)
        userService.deleteUser(userService.findByEmail("simon.lauwers4@gmail.com")!!)
        RestAssured.reset()
    }

    @Test
    @Transactional
    @DirtiesContext
    fun `register a single user`() {
        val reqBody = gson.toJson(RegisterDto("simonlauw", "simon.lauwers@telenet.be", "Badeendjes1972"))
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
    @Transactional
    @DirtiesContext
    fun `send confirmation email`() {
        Given {
            spec(requestSpecification).body(EmailDto("simon.lauwers4@gmail.com"))
        } When {
            post("/send-confirmation-email")
        } Then {
            assertThat().body(matchesJsonSchemaInClasspath("message-schema.json")).log()
            assertThat().body("message") { equalTo("email/confirmation-sent") }
            statusCode(HttpStatus.SC_OK)
        }
    }

    @Test
    @Transactional
    @DirtiesContext

    fun `confirm email`() {
        Given {
            val confToken = confirmationTokenService.findByUserEmail("simon.lauwers4@gmail.com")
            val confString = confToken!!.confirmationToken
            spec(requestSpecification).body(TokenDto(confString!!))
        } When {
            post("/confirm-account")
        } Then {
            assertThat().body(matchesJsonSchemaInClasspath("user-schema.json")).log()
            assertThat().body("isEnabled") { equalTo(true) }
            statusCode(HttpStatus.SC_OK)
        }
    }

    @Test
    @Transactional
    @DirtiesContext

    fun `login without email confirmation`() {
        Given {
            spec(requestSpecification).body(LoginDto("simon.lauwers4@gmail.com", "Clubvantstad01"))
        } When {
            post("/login")
        } Then {
            assertThat().body(matchesJsonSchemaInClasspath("message-schema.json")).log()
            assertThat().body("message") { equalTo("user/not-confirmed") }
            statusCode(HttpStatus.SC_BAD_REQUEST)
        }
    }
}
