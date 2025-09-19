package br.com.fiap.ecommerce.commons.controller.handler

import br.com.fiap.ecommerce.commons.entity.exception.ApiError
import br.com.fiap.ecommerce.commons.entity.exception.FieldError
import br.com.fiap.ecommerce.product.entity.exception.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGlobalException(
        e: RuntimeException
    ): ApiError {
        return ApiError(
            message = e.message ?: "Unexpected error",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ApiError {
        val errors = e.fieldErrors.map { FieldError(error = it.defaultMessage ?: e.message, field = it.field) }

        return ApiError(
            message = "Validation failed",
            status = HttpStatus.BAD_REQUEST.value(),
            errors = errors
        )
    }

    @ExceptionHandler(ProductNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleProductNotFoundException(
        e: Exception
    ): ApiError {
        return ApiError(
            message = e.message,
            status = HttpStatus.NOT_FOUND.value()
        )
    }
    
}