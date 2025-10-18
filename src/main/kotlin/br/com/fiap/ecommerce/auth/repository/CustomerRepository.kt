package br.com.fiap.ecommerce.auth.repository

import br.com.fiap.ecommerce.auth.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface CustomerRepository : JpaRepository<Customer, UUID> {

    fun findByEmail(email: String): Optional<Customer>

}