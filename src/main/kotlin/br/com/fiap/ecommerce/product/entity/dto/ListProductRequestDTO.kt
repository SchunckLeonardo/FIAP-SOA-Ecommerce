package br.com.fiap.ecommerce.product.entity.dto

import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum

data class ListProductRequestDTO(
    val productName: String? = null,
    val productCategory: ProductCategoryEnum? = null,
    val page: Int,
    val size: Int
)
