package br.com.fiap.ecommerce.auth.entity

import br.com.fiap.ecommerce.commons.entity.enums.GrantedAuthoritiesEnums
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserAuthenticated(
    private val customer: Customer
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return if (this.customer.isAdmin) {
            listOf(
                SimpleGrantedAuthority(GrantedAuthoritiesEnums.READ.permissionName),
                SimpleGrantedAuthority(GrantedAuthoritiesEnums.WRITE.permissionName)
            )
        } else {
            listOf(
                SimpleGrantedAuthority(GrantedAuthoritiesEnums.READ.permissionName)
            )
        }
    }

    override fun getPassword(): String? {
        return customer.passwordEncoded
    }

    override fun getUsername(): String? {
        return customer.email
    }

}