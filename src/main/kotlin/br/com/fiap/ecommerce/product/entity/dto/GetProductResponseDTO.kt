package br.com.fiap.ecommerce.product.entity.dto

data class GetProductResponseDTO(
    val name: String,
    val description: String? = null,
    val price: Double,
    val category: String,
    val amountAvailable: Int,
    val amountSold: Int
)
