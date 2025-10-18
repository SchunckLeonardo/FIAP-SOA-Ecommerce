package br.com.fiap.ecommerce.product.controller

import br.com.fiap.ecommerce.product.entity.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Products Management", description = "APIs for managing products in the e-commerce system")
interface ProductController {

    @Operation(summary = "Register a new product", description = "Creates a new product in the e-commerce system")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Product created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input data"),
        ApiResponse(responseCode = "403", description = "Unauthorized access"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun register(
        request: RegisterProductRequestDTO
    ): ResponseEntity<RegisterProductResponseDTO>

    @Operation(summary = "List products", description = "Retrieves a list of products with optional filtering and pagination")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        ApiResponse(responseCode = "400", description = "Invalid query parameters"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun listProducts(
        page: Int?,
        size: Int?,
        productName: String?,
        productCategory: String?
    ): ResponseEntity<List<ListProductResponseDTO>>

    @Operation(summary = "Get product by ID", description = "Retrieves a product by its unique identifier")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Product not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getProductById(
        id: String
    ): ResponseEntity<GetProductResponseDTO>

    @Operation(summary = "Delete a product", description = "Deletes a product by its unique identifier")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        ApiResponse(responseCode = "404", description = "Product not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun delete(
        id: String
    ): ResponseEntity<Any>

    @Operation(summary = "Update a product", description = "Updates the details of an existing product")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Product updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input data"),
        ApiResponse(responseCode = "404", description = "Product not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun update(
        id: String,
        request: UpdateProductRequestDTO
    ): ResponseEntity<Any>

}