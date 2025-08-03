package com.bluelock.irongate.controller

import com.bluelock.irongate.dto.*
import com.bluelock.irongate.security.UserDetailsImpl
import com.bluelock.irongate.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthService
) {
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<AuthenticationResponse> {
        return try {
            val response = authenticationService.register(request)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun authenticate(
        @Valid @RequestBody request: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse> {
        return try {
            val response = authenticationService.authenticate(request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @Valid @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<RefreshTokenResponse> {
        return try {
            val response = authenticationService.refreshToken(request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody request: Map<String, String>,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        return try {
            val refreshToken = request["refreshToken"] ?: ""
            authenticationService.logout(refreshToken)
            ResponseEntity.ok(mapOf("message" to "Logout exitoso"))
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(mapOf("error" to "Error durante logout"))
        }
    }

    @PostMapping("/logout-all")
    fun logoutAll(
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        return try {
            val userDetails = authentication.principal as UserDetailsImpl
            val user = userDetails.getUser()
            authenticationService.logoutAll(user)
            ResponseEntity.ok(mapOf("message" to "Logout de todos los dispositivos exitoso"))
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(mapOf("error" to "Error durante logout"))
        }
    }

    @GetMapping("/me")
    fun getCurrentUser(
        authentication: Authentication
    ): ResponseEntity<UserResponse> {
        val userDetails = authentication.principal as UserDetailsImpl
        val user = userDetails.getUser()
        return ResponseEntity.ok(UserResponse.fromUser(user))
    }

    @GetMapping("/validate")
    fun validateToken(
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val authHeader = httpRequest.getHeader("Authorization")
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            ResponseEntity.ok(mapOf(
                "valid" to true,
                "message" to "Token válido"
            ))
        } else {
            ResponseEntity.badRequest().body(mapOf(
                "valid" to false,
                "message" to "Token inválido"
            ))
        }
    }
}