package br.com.fiap.ecommerce.auth.controller

import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationRequestDTO
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationResponseDTO
import br.com.fiap.ecommerce.auth.entity.dto.RegisterRequestDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Authentication Management", description = "APIs for managing user authentication and registration")
interface AuthenticationController {

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successful authentication"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "401", description = "Invalid credentials"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun authenticate(authenticateRequestDTO: AuthenticationRequestDTO): ResponseEntity<AuthenticationResponseDTO>

    @Operation(summary = "Register new user", description = "Registers a new user in the e-commerce system")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "User registered successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request"),
        ApiResponse(responseCode = "409", description = "User already exists"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun register(registerRequestDTO: RegisterRequestDTO): ResponseEntity<Any>

}