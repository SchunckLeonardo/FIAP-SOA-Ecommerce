package br.com.fiap.ecommerce.product.controller.impl

import br.com.fiap.ecommerce.product.entity.dto.*
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import br.com.fiap.ecommerce.product.entity.exception.ProductNotFoundException
import br.com.fiap.ecommerce.product.service.ProductService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(
    controllers = [ProductControllerImpl::class],
    excludeFilters = [
        org.springframework.context.annotation.ComponentScan.Filter(
            type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
            classes = [br.com.fiap.ecommerce.commons.filter.SecurityFilter::class]
        )
    ]
)
class ProductControllerImplTest {

    @org.springframework.boot.test.context.TestConfiguration
    class TestSecurityConfig {
        @org.springframework.context.annotation.Bean
        fun securityFilterChain(http: org.springframework.security.config.annotation.web.builders.HttpSecurity): org.springframework.security.web.SecurityFilterChain {
            return http
                .csrf { it.disable() }
                .authorizeHttpRequests { auth ->
                    auth
                        .requestMatchers("/v1/auth/login", "/v1/auth/register").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/v1/products").hasAuthority("ROLE_WRITE")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/v1/products/**").hasAuthority("ROLE_WRITE")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/v1/products/**").hasAuthority("ROLE_WRITE")
                        .anyRequest().authenticated()
                }
                .build()
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val sampleProductId = UUID.randomUUID()

    @Test
    @WithMockUser(authorities = ["ROLE_WRITE"])
    fun `should register product successfully`() {
        // Given
        val request = RegisterProductRequestDTO(
            name = "Test Product",
            description = "Test Description",
            category = ProductCategoryEnum.TECHNOLOGY,
            price = 99.99,
            amountAvailable = 10
        )

        val response = RegisterProductResponseDTO(
            id = sampleProductId.toString(),
            name = "Test Product",
            description = "Test Description",
            category = "TECHNOLOGY",
            price = 99.99,
            amountAvailable = 10
        )

        every { productService.registerProduct(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/v1/products/$sampleProductId"))
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.category").value("TECHNOLOGY"))
            .andExpect(jsonPath("$.price").value(99.99))

        verify { productService.registerProduct(any()) }
    }

    @Test
    @WithMockUser(authorities = ["ROLE_READ"])
    fun `should list products successfully`() {
        // Given
        val products = listOf(
            ListProductResponseDTO(
                id = sampleProductId.toString(),
                name = "Test Product",
                description = "Test Description",
                category = "TECHNOLOGY",
                price = 99.99,
                amountAvailable = 10
            )
        )

        every { productService.listAllProducts(any()) } returns products

        // When & Then
        mockMvc.perform(
            get("/v1/products")
                .param("page", "0")
                .param("size", "10")
                .param("productName", "Test")
                .param("productCategory", "TECHNOLOGY")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Test Product"))
            .andExpect(jsonPath("$[0].category").value("TECHNOLOGY"))

        verify { productService.listAllProducts(any()) }
    }

    @Test
    @WithMockUser(authorities = ["ROLE_READ"])
    fun `should get product by id successfully`() {
        // Given
        val response = GetProductResponseDTO(
            name = "Test Product",
            description = "Test Description",
            price = 99.99,
            category = "TECHNOLOGY",
            amountAvailable = 10,
            amountSold = 5
        )

        every { productService.getProductById(sampleProductId) } returns response

        // When & Then
        mockMvc.perform(get("/v1/products/$sampleProductId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.category").value("TECHNOLOGY"))
            .andExpect(jsonPath("$.amountSold").value(5))

        verify { productService.getProductById(sampleProductId) }
    }

    @Test
    @WithMockUser(authorities = ["ROLE_READ"])
    fun `should return 404 when product not found`() {
        // Given
        every { productService.getProductById(sampleProductId) } throws ProductNotFoundException()

        // When & Then
        mockMvc.perform(get("/v1/products/$sampleProductId"))
            .andExpect(status().isNotFound)

        verify { productService.getProductById(sampleProductId) }
    }

    @Test
    @WithMockUser(authorities = ["ROLE_WRITE"])
    fun `should delete product successfully`() {
        // Given
        every { productService.deleteProduct(sampleProductId) } just runs

        // When & Then
        mockMvc.perform(delete("/v1/products/$sampleProductId"))
            .andExpect(status().isNoContent)

        verify { productService.deleteProduct(sampleProductId) }
    }

    @Test
    @WithMockUser(authorities = ["ROLE_WRITE"])
    fun `should update product successfully`() {
        // Given
        val request = UpdateProductRequestDTO(
            name = "Updated Product",
            price = 199.99
        )

        every { productService.updateProduct(sampleProductId, any()) } just runs

        // When & Then
        mockMvc.perform(
            put("/v1/products/$sampleProductId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNoContent)

        verify { productService.updateProduct(sampleProductId, any()) }
    }

    @Test
    @WithMockUser(authorities = ["ROLE_READ"])
    fun `should return 403 when user lacks WRITE permission for POST`() {
        // Given
        val request = RegisterProductRequestDTO(
            name = "Test Product",
            category = ProductCategoryEnum.TECHNOLOGY,
            price = 99.99,
            amountAvailable = 10
        )

        // When & Then
        mockMvc.perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
    }
}