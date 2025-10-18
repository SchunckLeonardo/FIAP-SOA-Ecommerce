package br.com.fiap.ecommerce.auth.controller

import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationRequestDTO
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationResponseDTO
import br.com.fiap.ecommerce.auth.entity.dto.RegisterRequestDTO
import org.springframework.http.ResponseEntity

interface AuthenticationController {

    fun authenticate(authenticateRequestDTO: AuthenticationRequestDTO): ResponseEntity<AuthenticationResponseDTO>
    fun register(registerRequestDTO: RegisterRequestDTO): ResponseEntity<Any>

}