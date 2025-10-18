package br.com.fiap.ecommerce.product.controller

import br.com.fiap.ecommerce.product.entity.dto.*
import org.springframework.http.ResponseEntity

interface ProductController {

    fun register(
        request: RegisterProductRequestDTO
    ): ResponseEntity<RegisterProductResponseDTO>

    fun listProducts(
        page: Int?,
        size: Int?,
        productName: String?,
        productCategory: String?
    ): ResponseEntity<List<ListProductResponseDTO>>

    fun getProductById(
        id: String
    ): ResponseEntity<GetProductResponseDTO>

    fun delete(
        id: String
    ): ResponseEntity<Any>

    fun update(
        id: String,
        request: UpdateProductRequestDTO
    ): ResponseEntity<Any>

}