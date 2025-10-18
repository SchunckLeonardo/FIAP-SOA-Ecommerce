package br.com.fiap.ecommerce.auth.service.impl

import br.com.fiap.ecommerce.auth.entity.UserAuthenticated
import br.com.fiap.ecommerce.auth.service.JwtService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class JwtServiceImpl(
    @param:Value("\${jwt.secret}")
    private val jwtSecret: String
) : JwtService {

    private final val APPLICATION_NAME = "ecommerce-api"
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    override fun generateToken(userAuthenticated: UserAuthenticated): String {
        return try {
            JWT.create()
                .withIssuer(APPLICATION_NAME)
                .withSubject(userAuthenticated.username)
                .withExpiresAt(genExpirationDate())
                .sign(algorithm)
        } catch (exception: JWTCreationException) {
            throw RuntimeException(exception.message, exception)
        }
    }

    override fun validateToken(token: String): String {
        return try {
            JWT.require(algorithm)
                .withIssuer(APPLICATION_NAME)
                .build()
                .verify(token)
                .subject
        } catch (exception: JWTVerificationException) {
            throw RuntimeException(exception.message, exception)
        }
    }

    private fun genExpirationDate(): Instant {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"))
    }

}