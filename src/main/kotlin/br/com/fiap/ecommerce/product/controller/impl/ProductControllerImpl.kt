package br.com.fiap.ecommerce.product.controller.impl

import br.com.fiap.ecommerce.product.controller.ProductController
import br.com.fiap.ecommerce.product.entity.dto.GetProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.ListProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.ListProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.UpdateProductRequestDTO
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
import java.util.UUID

@RestController
@RequestMapping("/v1/products")
class ProductControllerImpl(
    private val productService: ProductService
): ProductController {

    @PostMapping
    override fun register(
        @RequestBody @Valid request: RegisterProductRequestDTO
    ): ResponseEntity<RegisterProductResponseDTO> {
        val response = productService.registerProduct(request)
        val uri = URI.create("/v1/products/${response.id}")
        return ResponseEntity.created(uri).body(response)
    }

    @GetMapping
    override fun listProducts(
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
    override fun getProductById(
        @PathVariable id: String
    ): ResponseEntity<GetProductResponseDTO> {
        val response = productService.getProductById(
            id = UUID.fromString(id)
        )
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    override fun delete(
        @PathVariable id: String
    ): ResponseEntity<Any> {
        productService.deleteProduct(
            id = UUID.fromString(id)
        )
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    override fun update(
        @PathVariable id: String,
        @RequestBody @Valid request: UpdateProductRequestDTO
    ): ResponseEntity<Any> {
        productService.updateProduct(
            id = UUID.fromString(id),
            dto = request
        )
        return ResponseEntity.noContent().build()
    }

}