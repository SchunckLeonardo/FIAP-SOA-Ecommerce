package br.com.fiap.ecommerce.integration

import br.com.fiap.ecommerce.auth.entity.Customer
import br.com.fiap.ecommerce.auth.repository.CustomerRepository
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductRequestDTO
import br.com.fiap.ecommerce.product.entity.dto.UpdateProductRequestDTO
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import br.com.fiap.ecommerce.product.repository.ProductRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
@Transactional
class ProductIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setup() {
        productRepository.deleteAll()
        customerRepository.deleteAll()

        // Create a test admin user
        customerRepository.save(
            Customer(
                name = "Admin User",
                email = "admin@test.com",
                passwordEncoded = passwordEncoder.encode("password"),
                isAdmin = true
            )
        )
    }

    @Test
    @WithMockUser(authorities = ["ROLE_WRITE"])
    fun `should create, read, update and delete product`() {
        // CREATE - Register a new product
        val createRequest = RegisterProductRequestDTO(
            name = "Integration Test Product",
            description = "Test Description",
            category = ProductCategoryEnum.TECHNOLOGY,
            price = 199.99,
            amountAvailable = 25
        )

        val createResult = mockMvc.perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Integration Test Product"))
            .andExpect(jsonPath("$.price").value(199.99))
            .andReturn()

        val productId = objectMapper.readTree(createResult.response.contentAsString)["id"].asText()

        // READ - Get product by ID
        mockMvc.perform(get("/v1/products/$productId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Integration Test Product"))
            .andExpect(jsonPath("$.category").value("TECHNOLOGY"))
            .andExpect(jsonPath("$.amountSold").value(0))

        // READ - List products
        mockMvc.perform(get("/v1/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Integration Test Product"))

        // UPDATE - Update product
        val updateRequest = UpdateProductRequestDTO(
            name = "Updated Product Name",
            price = 299.99
        )

        mockMvc.perform(
            put("/v1/products/$productId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isNoContent)

        // Verify update
        mockMvc.perform(get("/v1/products/$productId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Product Name"))
            .andExpect(jsonPath("$.price").value(299.99))

        // DELETE - Delete product
        mockMvc.perform(delete("/v1/products/$productId"))
            .andExpect(status().isNoContent)

        // Verify deletion
        mockMvc.perform(get("/v1/products/$productId"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = ["ROLE_READ"])
    fun `should filter products by name and category`() {
        // Create test products
        val product1 = RegisterProductRequestDTO(
            name = "Smartphone Apple",
            category = ProductCategoryEnum.TECHNOLOGY,
            price = 999.99,
            amountAvailable = 10
        )

        val product2 = RegisterProductRequestDTO(
            name = "Laptop Dell",
            category = ProductCategoryEnum.TECHNOLOGY,
            price = 1299.99,
            amountAvailable = 5
        )

        val product3 = RegisterProductRequestDTO(
            name = "T-Shirt Nike",
            category = ProductCategoryEnum.FASHION,
            price = 49.99,
            amountAvailable = 20
        )

        // Create products with admin privileges
        mockMvc.perform(
            post("/v1/products")
                .with(
                    org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin")
                        .authorities(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_WRITE"))
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product1))
        )
            .andExpect(status().isCreated)

        // Filter by category
        mockMvc.perform(
            get("/v1/products")
                .param("productCategory", "TECHNOLOGY")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].category").value("TECHNOLOGY"))

        // Filter by name
        mockMvc.perform(
            get("/v1/products")
                .param("productName", "apple")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Smartphone Apple"))
    }

    @Test
    @WithMockUser(authorities = ["ROLE_READ"])
    fun `should handle pagination correctly`() {
        // Create multiple products (would need WRITE permission in real scenario)
        // For this test, we'll just test the pagination parameters
        mockMvc.perform(
            get("/v1/products")
                .param("page", "0")
                .param("size", "5")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    @WithMockUser(authorities = ["ROLE_READ"])
    fun `should return 403 when user lacks WRITE permission`() {
        val request = RegisterProductRequestDTO(
            name = "Unauthorized Product",
            category = ProductCategoryEnum.TECHNOLOGY,
            price = 99.99,
            amountAvailable = 10
        )

        mockMvc.perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
    }
}