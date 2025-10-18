package br.com.fiap.ecommerce.commons.entity.enums

enum class GrantedAuthoritiesEnums(val permissionName: String) {
    READ("ROLE_READ"),
    WRITE("ROLE_WRITE")
}