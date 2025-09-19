package br.com.fiap.ecommerce.product.entity.dto

data class RegisterProductResponseDTO(
    val id: String,
    val name: String,
    val description: String? = null,
    val category: String,
    val price: Double,
    val amountAvailable: Int
)
