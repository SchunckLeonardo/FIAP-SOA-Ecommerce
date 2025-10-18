package br.com.fiap.ecommerce.auth.service

import br.com.fiap.ecommerce.auth.entity.UserAuthenticated

interface JwtService {

    fun generateToken(userAuthenticated: UserAuthenticated): String
    fun validateToken(token: String): String

}