package br.com.fiap.ecommerce.product.entity.dto

import br.com.fiap.ecommerce.product.entity.Product
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class RegisterProductRequestDTO(

    @field:NotBlank(message = "Product name must not be blank")
    val name: String,

    val description: String? = null,
    val category: ProductCategoryEnum,

    @field:Positive(message = "Price must be a positive value")
    val price: Double,

    @field:Positive(message = "Amount available must be a positive value")
    val amountAvailable: Int
)

fun RegisterProductRequestDTO.toProduct(): Product =
    Product(
        name = name,
        description = description,
        category = category,
        price = price,
        amountAvailable = amountAvailable
    )