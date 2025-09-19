package br.com.fiap.ecommerce.product.repository

import br.com.fiap.ecommerce.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.awt.print.Pageable
import java.util.UUID

@Repository
interface ProductRepository : JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

}