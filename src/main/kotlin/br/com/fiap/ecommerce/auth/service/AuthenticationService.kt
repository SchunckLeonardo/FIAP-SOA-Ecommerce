package br.com.fiap.ecommerce.auth.service

import br.com.fiap.ecommerce.auth.entity.Customer
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationRequestDTO
import br.com.fiap.ecommerce.auth.entity.dto.AuthenticationResponseDTO
import br.com.fiap.ecommerce.auth.entity.dto.CustomerDTO
import br.com.fiap.ecommerce.auth.entity.dto.RegisterRequestDTO
import java.util.Optional

interface AuthenticationService {

    fun register(request: RegisterRequestDTO): CustomerDTO
    fun authenticate(request: AuthenticationRequestDTO): AuthenticationResponseDTO
    fun findCustomerByEmail(email: String): Optional<Customer>

}