package br.com.fiap.ecommerce.auth.controller.impl

import br.com.fiap.ecommerce.auth.controller.AuthenticationController
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationRequestDTO
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationResponseDTO
import br.com.fiap.ecommerce.auth.entity.dto.RegisterRequestDTO
import br.com.fiap.ecommerce.auth.service.AuthenticationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthenticationControllerImpl(
    private val authenticationService: AuthenticationService
): AuthenticationController {

    @PostMapping("/login")
    override fun authenticate(
        @RequestBody @Valid authenticateRequestDTO: AuthenticationRequestDTO
    ): ResponseEntity<AuthenticationResponseDTO> {
        val authenticationResponseDTO = authenticationService.authenticate(authenticateRequestDTO)
        return ResponseEntity.ok(authenticationResponseDTO)
    }

    @PostMapping("/register")
    override fun register(
        @RequestBody @Valid registerRequestDTO: RegisterRequestDTO
    ): ResponseEntity<Any> {
        authenticationService.register(registerRequestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

}