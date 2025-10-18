package br.com.fiap.ecommerce.auth.service.impl

import br.com.fiap.ecommerce.auth.entity.exception.CustomerNotFoundException
import br.com.fiap.ecommerce.auth.entity.toUserAuthenticated
import br.com.fiap.ecommerce.auth.repository.CustomerRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomerDetailsServiceImpl(
    private val customerRepository: CustomerRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        return username?.let {
            customerRepository
                .findByEmail(it)
                .orElseThrow { CustomerNotFoundException() }
                .toUserAuthenticated()
        }
    }

}