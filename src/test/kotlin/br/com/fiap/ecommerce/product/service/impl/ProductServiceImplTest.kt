package br.com.fiap.ecommerce.product.service.impl

import br.com.fiap.ecommerce.product.entity.Product
import br.com.fiap.ecommerce.product.entity.dto.ListProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.UpdateProductRequestDTO
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import br.com.fiap.ecommerce.product.entity.exception.ProductNotFoundException
import br.com.fiap.ecommerce.product.repository.ProductRepository
import br.com.fiap.ecommerce.product.service.specs.ProductSpecs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals

class ProductServiceImplTest {

    private val productRepository = mockk<ProductRepository>()
    private val productSpecs = mockk<ProductSpecs>()
    private lateinit var productService: ProductServiceImpl

    private val sampleProductId = UUID.randomUUID()
    private val sampleProduct = Product(
        id = sampleProductId,
        name = "Test Product",
        description = "Test Description",
        category = ProductCategoryEnum.TECHNOLOGY,
        price = 99.99,
        amountAvailable = 10,
        amountSold = 5,
        dhUpdated = LocalDateTime.now()
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        productService = ProductServiceImpl(productRepository, productSpecs)
    }

    @Test
    fun `should register product successfully`() {
        // Given
        val requestDTO = RegisterProductRequestDTO(
            name = "Test Product",
            description = "Test Description",
            category = ProductCategoryEnum.TECHNOLOGY,
            price = 99.99,
            amountAvailable = 10
        )

        every { productRepository.save(any<Product>()) } returns sampleProduct

        // When
        val result = productService.registerProduct(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals("Test Product", result.name)
        assertEquals("Test Description", result.description)
        assertEquals("TECHNOLOGY", result.category)
        assertEquals(99.99, result.price)
        assertEquals(10, result.amountAvailable)

        verify { productRepository.save(any<Product>()) }
    }

    @Test
    fun `should list all products with pagination`() {
        // Given
        val requestDTO = ListProductRequestDTO(
            productName = "Test",
            productCategory = ProductCategoryEnum.TECHNOLOGY,
            page = 0,
            size = 10
        )

        val pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "name")
        val mockSpec = mockk<Specification<Product>>()
        val page = PageImpl(listOf(sampleProduct))

        every { productSpecs.getSpecsAndValidate(requestDTO) } returns mockSpec
        every { productRepository.findAll(mockSpec, pageable) } returns page

        // When
        val result = productService.listAllProducts(requestDTO)

        // Then
        assertEquals(1, result.size)
        assertEquals("Test Product", result[0].name)
        assertEquals("TECHNOLOGY", result[0].category)

        verify { productSpecs.getSpecsAndValidate(requestDTO) }
        verify { productRepository.findAll(mockSpec, pageable) }
    }

    @Test
    fun `should get product by id successfully`() {
        // Given
        every { productRepository.findById(sampleProductId) } returns Optional.of(sampleProduct)

        // When
        val result = productService.getProductById(sampleProductId)

        // Then
        assertEquals("Test Product", result.name)
        assertEquals("Test Description", result.description)
        assertEquals("TECHNOLOGY", result.category)
        assertEquals(99.99, result.price)
        assertEquals(10, result.amountAvailable)
        assertEquals(5, result.amountSold)

        verify { productRepository.findById(sampleProductId) }
    }

    @Test
    fun `should throw exception when product not found by id`() {
        // Given
        every { productRepository.findById(sampleProductId) } returns Optional.empty()

        // When & Then
        assertThrows<ProductNotFoundException> {
            productService.getProductById(sampleProductId)
        }

        verify { productRepository.findById(sampleProductId) }
    }

    @Test
    fun `should delete product successfully`() {
        // Given
        every { productRepository.findById(sampleProductId) } returns Optional.of(sampleProduct)
        every { productRepository.delete(sampleProduct) } just runs

        // When
        productService.deleteProduct(sampleProductId)

        // Then
        verify { productRepository.findById(sampleProductId) }
        verify { productRepository.delete(sampleProduct) }
    }

    @Test
    fun `should throw exception when deleting non-existent product`() {
        // Given
        every { productRepository.findById(sampleProductId) } returns Optional.empty()

        // When & Then
        assertThrows<ProductNotFoundException> {
            productService.deleteProduct(sampleProductId)
        }

        verify { productRepository.findById(sampleProductId) }
        verify(exactly = 0) { productRepository.delete(any<Product>()) }
    }

    @Test
    fun `should update product successfully`() {
        // Given
        val updateDTO = UpdateProductRequestDTO(
            name = "Updated Product",
            price = 199.99,
            amountAvailable = 20
        )

        val updatedProduct = sampleProduct.copy(
            name = "Updated Product",
            price = 199.99,
            amountAvailable = 20
        )

        every { productRepository.findById(sampleProductId) } returns Optional.of(sampleProduct)
        every { productRepository.save(any<Product>()) } returns updatedProduct

        // When
        productService.updateProduct(sampleProductId, updateDTO)

        // Then
        verify { productRepository.findById(sampleProductId) }
        verify { productRepository.save(match { product ->
            product.name == "Updated Product" &&
            product.price == 199.99 &&
            product.amountAvailable == 20
        }) }
    }

    @Test
    fun `should throw exception when updating non-existent product`() {
        // Given
        val updateDTO = UpdateProductRequestDTO(name = "Updated Product")
        every { productRepository.findById(sampleProductId) } returns Optional.empty()

        // When & Then
        assertThrows<ProductNotFoundException> {
            productService.updateProduct(sampleProductId, updateDTO)
        }

        verify { productRepository.findById(sampleProductId) }
        verify(exactly = 0) { productRepository.save(any()) }
    }
}