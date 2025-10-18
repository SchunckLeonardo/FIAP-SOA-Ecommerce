package br.com.fiap.ecommerce.product.service.impl

import br.com.fiap.ecommerce.product.entity.dto.GetProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.ListProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.ListProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.UpdateProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.toProduct
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import br.com.fiap.ecommerce.product.entity.exception.ProductNotFoundException
import br.com.fiap.ecommerce.product.entity.toGetProductResponseDTO
import br.com.fiap.ecommerce.product.entity.toListProductResponseDTO
import br.com.fiap.ecommerce.product.entity.toRegisterProductResponseDTO
import br.com.fiap.ecommerce.product.repository.ProductRepository
import br.com.fiap.ecommerce.product.service.ProductService
import br.com.fiap.ecommerce.product.service.specs.ProductSpecs
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productSpecs: ProductSpecs
): ProductService {

    override fun registerProduct(dto: RegisterProductRequestDTO): RegisterProductResponseDTO =
        productRepository.save(
            dto.toProduct()
        ).toRegisterProductResponseDTO()

    override fun listAllProducts(dto: ListProductRequestDTO): List<ListProductResponseDTO> {
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

    override fun getProductById(id: UUID): GetProductResponseDTO =
        productRepository.findById(id)
            .orElseThrow { ProductNotFoundException() }
            .toGetProductResponseDTO()

    override fun deleteProduct(id: UUID) {
        val product = productRepository.findById(id).orElseThrow { ProductNotFoundException() }
        productRepository.delete(product)
    }

    override fun updateProduct(id: UUID, dto: UpdateProductRequestDTO) {
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