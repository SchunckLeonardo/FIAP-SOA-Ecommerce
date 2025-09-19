package br.com.fiap.ecommerce.product.service

import br.com.fiap.ecommerce.product.entity.dto.*
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import br.com.fiap.ecommerce.product.entity.exception.ProductNotFoundException
import br.com.fiap.ecommerce.product.entity.toGetProductResponseDTO
import br.com.fiap.ecommerce.product.entity.toListProductResponseDTO
import br.com.fiap.ecommerce.product.entity.toRegisterProductResponseDTO
import br.com.fiap.ecommerce.product.repository.ProductRepository
import br.com.fiap.ecommerce.product.service.specs.ProductSpecs
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productSpecs: ProductSpecs
) {

    fun registerProduct(dto: RegisterProductRequestDTO): RegisterProductResponseDTO =
        productRepository.save(
            dto.toProduct()
        ).toRegisterProductResponseDTO()

    fun listAllProducts(dto: ListProductRequestDTO): List<ListProductResponseDTO> {
        val pageable = PageRequest.of(
            dto.page,
            dto.size,
            Sort.Direction.ASC,
            "name"
        )

        return productRepository
            .findAll(productSpecs.getSpecsAndValidate(dto), pageable)
            .map { it.toListProductResponseDTO() }
            .toList()
    }

    fun getProductById(id: UUID): GetProductResponseDTO =
        productRepository.findById(id)
            .orElseThrow { ProductNotFoundException() }
            .toGetProductResponseDTO()

    fun deleteProduct(id: UUID) {
        val product = productRepository.findById(id).orElseThrow { ProductNotFoundException() }
        productRepository.delete(product)
    }

    fun updateProduct(id: UUID, dto: UpdateProductRequestDTO) {
        val product = productRepository.findById(id).orElseThrow { ProductNotFoundException() }
        val updatedProduct = product.copy(
            name = dto.name ?: product.name,
            description = dto.description ?: product.description,
            category = dto.category?.let { ProductCategoryEnum.valueOf(it) } ?: product.category,
            price = dto.price ?: product.price,
            amountAvailable = dto.amountAvailable ?: product.amountAvailable,
            dhUpdated = LocalDateTime.now()
        )
        productRepository.save(updatedProduct)
    }

}