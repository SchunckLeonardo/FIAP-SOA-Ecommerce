package br.com.fiap.ecommerce.auth.entity.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class AuthenticationRequestDTO(

    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String

)
