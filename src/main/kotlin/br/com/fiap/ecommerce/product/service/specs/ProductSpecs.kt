package br.com.fiap.ecommerce.product.service.specs

import br.com.fiap.ecommerce.product.entity.Product
import br.com.fiap.ecommerce.product.entity.dto.ListProductRequestDTO
import br.com.fiap.ecommerce.product.entity.enums.ProductCategoryEnum
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component


/**
 *
 * Classe responsável por construir especificações dinâmicas para consultas de produtos.
 *
 * @author Leonardo Schunck
 * @since 1.0
 *
 * */
@Component
class ProductSpecs {

    private fun nameLike(name: String): Specification<Product> =
        Specification<Product> { root, _, cb ->
            cb.like(
                cb.upper(
                    root.get<String>("name")
                ),
                "%${name.uppercase()}%"
            )
        }

    private fun categoryEqual(category: ProductCategoryEnum): Specification<Product> =
        Specification<Product> { root, _, cb ->
            cb.equal(
                root.get<ProductCategoryEnum>("category"), category
            )
        }

    fun getSpecsAndValidate(request: ListProductRequestDTO): Specification<Product> {
        val specs = listOfNotNull(
            request.productName?.let { nameLike(it)},
            request.productCategory?.let { categoryEqual(it)}
        )

        return specs.fold(Specification { _, _, cb -> cb.conjunction() }) { acc, spec ->
            acc.and(spec)
        }
    }

}