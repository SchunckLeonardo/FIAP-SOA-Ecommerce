package br.com.fiap.ecommerce.product.controller

import br.com.fiap.ecommerce.product.entity.dto.*
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import br.com.fiap.ecommerce.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    fun register(
        @RequestBody @Valid request: RegisterProductRequestDTO
    ): ResponseEntity<RegisterProductResponseDTO> {
        val response = productService.registerProduct(request)
        val uri = URI.create("/v1/products/${response.id}")
        return ResponseEntity.created(uri).body(response)
    }

    @GetMapping
    fun listProducts(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestParam(required = false) productName: String?,
        @RequestParam(required = false) productCategory: String?
    ): ResponseEntity<List<ListProductResponseDTO>> {
        val response = productService.listAllProducts(
            ListProductRequestDTO(
                productName = productName,
                productCategory = productCategory?.let { ProductCategoryEnum.valueOf(it) },
                page = page ?: 0,
                size = size ?: 10
            )
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable id: String
    ): ResponseEntity<GetProductResponseDTO> {
        val response = productService.getProductById(
            id = java.util.UUID.fromString(id)
        )
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: String
    ): ResponseEntity<Any> {
        productService.deleteProduct(
            id = java.util.UUID.fromString(id)
        )
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody @Valid request: UpdateProductRequestDTO
    ): ResponseEntity<Any> {
        productService.updateProduct(
            id = java.util.UUID.fromString(id),
            dto = request
        )
        return ResponseEntity.noContent().build()
    }

}