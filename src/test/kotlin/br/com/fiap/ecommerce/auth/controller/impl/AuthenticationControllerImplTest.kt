package br.com.fiap.ecommerce.auth.controller.impl

import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationRequestDTO
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationResponseDTO
import br.com.fiap.ecommerce.auth.entity.dto.CustomerDTO
import br.com.fiap.ecommerce.auth.entity.dto.RegisterRequestDTO
import br.com.fiap.ecommerce.auth.entity.exception.BadAuthenticationException
import br.com.fiap.ecommerce.auth.entity.exception.CustomerAlreadyRegisteredException
import br.com.fiap.ecommerce.auth.service.AuthenticationService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(
    controllers = [AuthenticationControllerImpl::class],
    excludeFilters = [
        org.springframework.context.annotation.ComponentScan.Filter(
            type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
            classes = [br.com.fiap.ecommerce.commons.filter.SecurityFilter::class]
        )
    ]
)
class AuthenticationControllerImplTest {

    @org.springframework.boot.test.context.TestConfiguration
    class TestSecurityConfig {
        @org.springframework.context.annotation.Bean
        fun securityFilterChain(http: org.springframework.security.config.annotation.web.builders.HttpSecurity): org.springframework.security.web.SecurityFilterChain {
            return http
                .csrf { it.disable() }
                .authorizeHttpRequests { auth ->
                    auth
                        .requestMatchers("/v1/auth/login", "/v1/auth/register").permitAll()
                        .anyRequest().authenticated()
                }
                .build()
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should authenticate user successfully`() {
        // Given
        val request = AuthenticationRequestDTO(
            email = "test@example.com",
            password = "password123"
        )

        val response = AuthenticationResponseDTO(token = "jwt-token")

        every { authenticationService.authenticate(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("jwt-token"))

        verify { authenticationService.authenticate(any()) }
    }

    @Test
    fun `should return bad request when authentication fails`() {
        // Given
        val request = AuthenticationRequestDTO(
            email = "test@example.com",
            password = "wrongpassword"
        )

        every { authenticationService.authenticate(any()) } throws BadAuthenticationException()

        // When & Then
        mockMvc.perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verify { authenticationService.authenticate(any()) }
    }

    @Test
    fun `should register user successfully`() {
        // Given
        val request = RegisterRequestDTO(
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            isAdmin = false
        )

        val customerDTO = CustomerDTO(
            name = "Test User",
            email = "test@example.com"
        )

        every { authenticationService.register(any()) } returns customerDTO

        // When & Then
        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

        verify { authenticationService.register(any()) }
    }

    @Test
    fun `should return conflict when customer already exists`() {
        // Given
        val request = RegisterRequestDTO(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        every { authenticationService.register(any()) } throws CustomerAlreadyRegisteredException()

        // When & Then
        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)

        verify { authenticationService.register(any()) }
    }

    @Test
    fun `should return bad request for invalid email format`() {
        // Given
        val request = RegisterRequestDTO(
            name = "Test User",
            email = "invalid-email",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0].field").value("email"))
    }

    @Test
    fun `should return bad request for blank fields`() {
        // Given
        val request = RegisterRequestDTO(
            name = "",
            email = "",
            password = ""
        )

        // When & Then
        mockMvc.perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors").isArray)
            .andExpect(jsonPath("$.errors.length()").value(3))
    }
}