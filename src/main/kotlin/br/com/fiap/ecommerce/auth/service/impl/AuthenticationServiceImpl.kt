package br.com.fiap.ecommerce.auth.service.impl

import br.com.fiap.ecommerce.auth.entity.Customer
import br.com.fiap.ecommerce.auth.entity.UserAuthenticated
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationRequestDTO
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationResponseDTO
import br.com.fiap.ecommerce.auth.entity.dto.CustomerDTO
import br.com.fiap.ecommerce.auth.entity.dto.RegisterRequestDTO
import br.com.fiap.ecommerce.auth.entity.exception.BadAuthenticationException
import br.com.fiap.ecommerce.auth.entity.exception.CustomerAlreadyRegisteredException
import br.com.fiap.ecommerce.auth.entity.toCustomerDTO
import br.com.fiap.ecommerce.auth.repository.CustomerRepository
import br.com.fiap.ecommerce.auth.service.AuthenticationService
import br.com.fiap.ecommerce.auth.service.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationServiceImpl(
    private val customerRepository: CustomerRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
): AuthenticationService {

    override fun register(request: RegisterRequestDTO): CustomerDTO {
        findCustomerByEmail(request.email).ifPresent { throw CustomerAlreadyRegisteredException() }

        return customerRepository.save(
            Customer(
                name = request.name,
                email = request.email,
                passwordEncoded = passwordEncoder.encode(request.password),
                isAdmin = request.isAdmin
            )
        ).toCustomerDTO()
    }

    override fun authenticate(request: AuthenticationRequestDTO): AuthenticationResponseDTO {
        val customer = findCustomerByEmail(request.email)
            .orElseThrow {
                BadAuthenticationException()
            }

        if (!doesPasswordMatch(request.password, customer.passwordEncoded)) {
            throw BadAuthenticationException()
        }

        val usernameAndPassword = UsernamePasswordAuthenticationToken(request.email, request.password)
        val auth = authenticationManager.authenticate(usernameAndPassword)
        val token = jwtService.generateToken(auth.principal as UserAuthenticated)

        return AuthenticationResponseDTO(
            token = token
        )
    }

    override fun findCustomerByEmail(email: String): Optional<Customer> =
        customerRepository
            .findByEmail(email)

    private fun doesPasswordMatch(rawPassword: String, encodedPassword: String): Boolean =
        passwordEncoder.matches(rawPassword, encodedPassword)

}