package br.com.fiap.ecommerce.auth.entity

import br.com.fiap.ecommerce.auth.entity.dto.CustomerDTO
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tb_customer")
data class Customer(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column
    val name: String = "",

    @Column
    val email: String = "",

    @Column(name = "password_encoded")
    val passwordEncoded: String = "",

    @Column(name = "is_admin")
    val isAdmin: Boolean = false,

    @Column(name = "dh_created")
    val dhCreated: LocalDateTime = LocalDateTime.now()

)

fun Customer.toCustomerDTO() =
    CustomerDTO(
        name = name,
        email = email
    )

fun Customer.toUserAuthenticated() =
    UserAuthenticated(
        this
    )