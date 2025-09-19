package br.com.fiap.ecommerce.commons.entity.exception

data class FieldError(
    val error: String? = null,
    val field: String? = null
)
