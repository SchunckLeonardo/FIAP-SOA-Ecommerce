package br.com.fiap.ecommerce.auth.entity.exception

data class CustomerNotFoundException(
    override val message: String? = "Customer not found"
): RuntimeException(message)
