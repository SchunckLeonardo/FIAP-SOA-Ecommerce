
package br.com.fiap.ecommerce.integration

import br.com.fiap.ecommerce.auth.entity.Customer
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationRequestDTO
import br.com.fiap.ecommerce.auth.entity.dto.RegisterRequestDTO
import br.com.fiap.ecommerce.auth.repository.CustomerRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setup() {
        customerRepository.deleteAll()
    }

    @Test
    fun `should register and authenticate user successfully`() {
        // REGISTER - Create new user
        val registerRequest = RegisterRequestDTO(
            name = "Integration Test User",
            email = "integration@test.com",
            password = "password123",
            isAdmin = false
        )

        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isCreated)

        // Verify user was created in database
        val savedCustomer = customerRepository.findByEmail("integration@test.com")
        assert(savedCustomer.isPresent)
        assert(savedCustomer.get().name == "Integration Test User")

        // AUTHENTICATE - Login with the created user
        val authRequest = AuthenticationRequestDTO(
            email = "integration@test.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    fun `should not register user with existing email`() {
        // Pre-create a user
        customerRepository.save(
            Customer(
                name = "Existing User",
                email = "existing@test.com",
                passwordEncoded = passwordEncoder.encode("password"),
                isAdmin = false
            )
        )

        // Try to register with same email
        val registerRequest = RegisterRequestDTO(
            name = "New User",
            email = "existing@test.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Customer with this email is already registered"))
    }

    @Test
    fun `should not authenticate with wrong credentials`() {
        // Pre-create a user
        customerRepository.save(
            Customer(
                name = "Test User",
                email = "test@test.com",
                passwordEncoded = passwordEncoder.encode("correctpassword"),
                isAdmin = false
            )
        )

        // Try to authenticate with wrong password
        val authRequest = AuthenticationRequestDTO(
            email = "test@test.com",
            password = "wrongpassword"
        )

        mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should not authenticate non-existent user`() {
        val authRequest = AuthenticationRequestDTO(
            email = "nonexistent@test.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should validate request fields`() {
        // Test invalid email format
        val invalidEmailRequest = RegisterRequestDTO(
            name = "Test User",
            email = "invalid-email",
            password = "password123"
        )

        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0].field").value("email"))

        // Test blank fields
        val blankFieldsRequest = RegisterRequestDTO(
            name = "",
            email = "",
            password = ""
        )

        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blankFieldsRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors").isArray)
            .andExpect(jsonPath("$.errors.length()").value(3))
    }
}