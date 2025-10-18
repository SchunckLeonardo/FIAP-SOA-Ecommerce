package br.com.fiap.ecommerce.auth.service.impl

import br.com.fiap.ecommerce.auth.entity.Customer
import br.com.fiap.ecommerce.auth.entity.UserAuthenticated
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationRequestDTO
import br.com.fiap.ecommerce.auth.entity.dto.RegisterRequestDTO
import br.com.fiap.ecommerce.auth.entity.exception.BadAuthenticationException
import br.com.fiap.ecommerce.auth.entity.exception.CustomerAlreadyRegisteredException
import br.com.fiap.ecommerce.auth.repository.CustomerRepository
import br.com.fiap.ecommerce.auth.service.JwtService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthenticationServiceImplTest {

    private val customerRepository = mockk<CustomerRepository>()
    private val authenticationManager = mockk<AuthenticationManager>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtService = mockk<JwtService>()
    private lateinit var authenticationService: AuthenticationServiceImpl

    private val sampleCustomer = Customer(
        id = UUID.randomUUID(),
        name = "Test User",
        email = "test@example.com",
        passwordEncoded = "encodedPassword",
        isAdmin = false
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        authenticationService = AuthenticationServiceImpl(
            customerRepository,
            authenticationManager,
            passwordEncoder,
            jwtService
        )
    }

    @Test
    fun `should register customer successfully`() {
        // Given
        val request = RegisterRequestDTO(
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            isAdmin = false
        )

        every { customerRepository.findByEmail("test@example.com") } returns Optional.empty()
        every { passwordEncoder.encode("password123") } returns "encodedPassword"
        every { customerRepository.save(any<Customer>()) } returns sampleCustomer

        // When
        val result = authenticationService.register(request)

        // Then
        assertNotNull(result)
        assertEquals("Test User", result.name)
        assertEquals("test@example.com", result.email)

        verify { customerRepository.findByEmail("test@example.com") }
        verify { passwordEncoder.encode("password123") }
        verify { customerRepository.save(any<Customer>()) }
    }

    @Test
    fun `should throw exception when registering existing customer`() {
        // Given
        val request = RegisterRequestDTO(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        every { customerRepository.findByEmail("test@example.com") } returns Optional.of(sampleCustomer)

        // When & Then
        assertThrows<CustomerAlreadyRegisteredException> {
            authenticationService.register(request)
        }

        verify { customerRepository.findByEmail("test@example.com") }
        verify(exactly = 0) { customerRepository.save(any()) }
    }

    @Test
    fun `should authenticate user successfully`() {
        // Given
        val request = AuthenticationRequestDTO(
            email = "test@example.com",
            password = "password123"
        )

        val userAuthenticated = UserAuthenticated(sampleCustomer)
        val authentication = mockk<org.springframework.security.core.Authentication>()

        every { customerRepository.findByEmail("test@example.com") } returns Optional.of(sampleCustomer)
        every { passwordEncoder.matches("password123", "encodedPassword") } returns true
        every { authenticationManager.authenticate(any()) } returns authentication
        every { authentication.principal } returns userAuthenticated
        every { jwtService.generateToken(userAuthenticated) } returns "jwt-token"

        // When
        val result = authenticationService.authenticate(request)

        // Then
        assertEquals("jwt-token", result.token)

        verify { customerRepository.findByEmail("test@example.com") }
        verify { passwordEncoder.matches("password123", "encodedPassword") }
        verify { authenticationManager.authenticate(any()) }
        verify { jwtService.generateToken(userAuthenticated) }
    }

    @Test
    fun `should throw exception when customer not found during authentication`() {
        // Given
        val request = AuthenticationRequestDTO(
            email = "nonexistent@example.com",
            password = "password123"
        )

        every { customerRepository.findByEmail("nonexistent@example.com") } returns Optional.empty()

        // When & Then
        assertThrows<BadAuthenticationException> {
            authenticationService.authenticate(request)
        }

        verify { customerRepository.findByEmail("nonexistent@example.com") }
        verify(exactly = 0) { authenticationManager.authenticate(any()) }
    }

    @Test
    fun `should throw exception when password doesn't match`() {
        // Given
        val request = AuthenticationRequestDTO(
            email = "test@example.com",
            password = "wrongpassword"
        )

        every { customerRepository.findByEmail("test@example.com") } returns Optional.of(sampleCustomer)
        every { passwordEncoder.matches("wrongpassword", "encodedPassword") } returns false

        // When & Then
        assertThrows<BadAuthenticationException> {
            authenticationService.authenticate(request)
        }

        verify { customerRepository.findByEmail("test@example.com") }
        verify { passwordEncoder.matches("wrongpassword", "encodedPassword") }
        verify(exactly = 0) { authenticationManager.authenticate(any()) }
    }

    @Test
    fun `should find customer by email successfully`() {
        // Given
        every { customerRepository.findByEmail("test@example.com") } returns Optional.of(sampleCustomer)

        // When
        val result = authenticationService.findCustomerByEmail("test@example.com")

        // Then
        assertEquals(sampleCustomer, result.get())
        verify { customerRepository.findByEmail("test@example.com") }
    }

    @Test
    fun `should return empty when customer not found by email`() {
        // Given
        every { customerRepository.findByEmail("nonexistent@example.com") } returns Optional.empty()

        // When
        val result = authenticationService.findCustomerByEmail("nonexistent@example.com")

        // Then
        assertEquals(Optional.empty(), result)
        verify { customerRepository.findByEmail("nonexistent@example.com") }
    }
}