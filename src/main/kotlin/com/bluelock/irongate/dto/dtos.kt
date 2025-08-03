package com.bluelock.irongate.dto

import com.bluelock.irongate.entity.Role
import com.bluelock.irongate.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class RegisterRequest(
    @field:NotBlank(message = "El nombre de usuario es requerido")
    @field:Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    val username: String,

    @field:NotBlank(message = "El email es requerido")
    @field:Email(message = "Formato de email inválido")
    val email: String,

    @field:NotBlank(message = "La contraseña es requerida")
    @field:Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    val password: String,

    @field:NotBlank(message = "El nombre es requerido")
    val firstName: String,

    @field:NotBlank(message = "El apellido es requerido")
    val lastName: String,

    val role: Role = Role.USER
)

data class AuthenticationRequest(
    @field:NotBlank(message = "El nombre de usuario es requerido")
    val username: String,

    @field:NotBlank(message = "La contraseña es requerida")
    val password: String
)

// Response DTOs
data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 86400, // 24 horas en segundos
    val user: UserResponse
)

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val isEnabled: Boolean
) {
    companion object {
        fun fromUser(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                role = user.role,
                isEnabled = user.isEnabled
            )
        }
    }
}

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)

data class ValidationErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val validationErrors: Map<String, String>
)