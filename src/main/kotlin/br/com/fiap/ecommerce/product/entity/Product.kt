package br.com.fiap.ecommerce.product.entity

import br.com.fiap.ecommerce.product.entity.dto.GetProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.ListProductResponseDTO
import br.com.fiap.ecommerce.product.entity.dto.RegisterProductResponseDTO
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tb_product")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column
    val name: String = "",

    @Column(nullable = true)
    val description: String? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val category: ProductCategoryEnum = ProductCategoryEnum.OTHER,

    @Column
    val price: Double = 0.0,

    @Column(name = "amount_available")
    val amountAvailable: Int = 0,

    @Column(name = "amount_sold")
    val amountSold: Int = 0,

    @Column(name = "dh_updated")
    val dhUpdated: LocalDateTime = LocalDateTime.now()

)

fun Product.toRegisterProductResponseDTO(): RegisterProductResponseDTO =
    RegisterProductResponseDTO(
        id = id.toString(),
        name = name,
        description = description,
        category = category.name,
        price = price,
        amountAvailable = amountAvailable
    )

fun Product.toListProductResponseDTO(): ListProductResponseDTO =
    ListProductResponseDTO(
        id = id.toString(),
        name = name,
        description = description,
        category = category.name,
        price = price,
        amountAvailable = amountAvailable
    )

fun Product.toGetProductResponseDTO(): GetProductResponseDTO =
    GetProductResponseDTO(
        name = name,
        description = description,
        category = category.name,
        price = price,
        amountAvailable = amountAvailable,
        amountSold = amountSold
    )