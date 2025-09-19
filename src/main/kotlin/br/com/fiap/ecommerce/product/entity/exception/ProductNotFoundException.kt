package br.com.fiap.ecommerce.product.entity.exception

data class ProductNotFoundException(
    override val message: String = "Product not found"
): RuntimeException(message)
