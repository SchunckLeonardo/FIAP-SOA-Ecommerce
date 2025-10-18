package br.com.fiap.ecommerce.auth.entity.exception

import java.lang.RuntimeException

data class BadAuthenticationException(
    override val message: String = "Occurred a bad authentication attempt"
): RuntimeException(message)
