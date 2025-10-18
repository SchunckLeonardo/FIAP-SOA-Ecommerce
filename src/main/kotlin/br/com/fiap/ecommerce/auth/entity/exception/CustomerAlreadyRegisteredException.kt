package br.com.fiap.ecommerce.auth.entity.exception

data class CustomerAlreadyRegisteredException(
    override val message: String = "Customer with this email is already registered"
): RuntimeException(message)
