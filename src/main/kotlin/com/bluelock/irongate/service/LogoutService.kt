package com.bluelock.irongate.service

import com.bluelock.irongate.repository.RefreshTokenRepository
import com.bluelock.irongate.security.JwtService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
class LogoutService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService
) : LogoutHandler {
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }

        val jwt = authHeader.substring(7)
        try {
            // Extraer información del token
            val username = jwtService.extractUsername(jwt)

            // Obtener refresh token del cuerpo de la petición si está disponible
            val refreshToken = request.getParameter("refreshToken")

            if (refreshToken != null) {
                refreshTokenRepository.revokeToken(refreshToken)
            }

            // Limpiar el contexto de seguridad
            SecurityContextHolder.clearContext()

        } catch (e: Exception) {
            // Log del error pero continuar con el logout
            println("Error durante logout: ${e.message}")
        }
    }
}