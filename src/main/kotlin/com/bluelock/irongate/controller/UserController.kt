package com.bluelock.irongate.controller

import com.bluelock.irongate.dto.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
@SecurityRequirement(name = "BearerAuth")
class UserController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun getUserProfile(authentication: Authentication): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val userInfo = mapOf(
            "message" to "Acceso a perfil de usuario",
            "username" to authentication.name,
            "authorities" to authentication.authorities.map { it.authority }
        )

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Perfil de usuario obtenido exitosamente",
                data = userInfo
            )
        )
    }

    @GetMapping("/dashboard")
    fun getDashboard(authentication: Authentication): ResponseEntity<ApiResponse<Map<String, String>>> {
        val dashboard = mapOf(
            "message" to "Bienvenido al dashboard",
            "user" to authentication.name
        )

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Dashboard cargado",
                data = dashboard
            )
        )
    }
}