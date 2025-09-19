package br.com.fiap.ecommerce.commons.entity.exception

data class ApiError(
    val message: String? = null,
    val status: Int? = null,
    val errors: List<FieldError> = emptyList()
)
