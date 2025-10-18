package br.com.fiap.ecommerce.product.service

import br.com.fiap.ecommerce.product.entity.dto.GetProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.ListProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.ListProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.UpdateProductRequestDTO
import java.util.UUID

interface ProductService {

    fun registerProduct(dto: RegisterProductRequestDTO): RegisterProductResponseDTO
    fun listAllProducts(dto: ListProductRequestDTO): List<ListProductResponseDTO>
    fun getProductById(id: UUID): GetProductResponseDTO
    fun deleteProduct(id: UUID)
    fun updateProduct(id: UUID, dto: UpdateProductRequestDTO)

}