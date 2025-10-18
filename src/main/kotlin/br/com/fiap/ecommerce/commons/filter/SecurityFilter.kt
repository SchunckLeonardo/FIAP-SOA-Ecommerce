package br.com.fiap.ecommerce.commons.filter

import br.com.fiap.ecommerce.auth.entity.toUserAuthenticated
import br.com.fiap.ecommerce.auth.repository.CustomerRepository
import br.com.fiap.ecommerce.auth.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityFilter(
    private val jwtService: JwtService,
    private val customerRepository: CustomerRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = recoverToken(request)

        if (token != null) {
            val email = jwtService.validateToken(token)
            val user = customerRepository.findByEmail(email)

            val authentication = UsernamePasswordAuthenticationToken(user, null, user.get().toUserAuthenticated().authorities)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    fun recoverToken(request: HttpServletRequest): String? {
        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader?.isBlank() == true) {
            return null
        }

        return authHeader?.replace("Bearer ", "")
    }

}