package br.com.fiap.ecommerce.product.entity.dto

import jakarta.validation.constraints.Positive

data class UpdateProductRequestDTO(
    val name: String? = null,
    val description: String? = null,
    val category: String? = null,

    @field:Positive(message = "Price must be positive")
    val price: Double? = null,

    @field:Positive(message = "Amount available must be positive")
    val amountAvailable: Int? = null
)
