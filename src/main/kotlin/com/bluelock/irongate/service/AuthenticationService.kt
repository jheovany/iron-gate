package com.bluelock.irongate.service

import com.bluelock.irongate.dto.*
import com.bluelock.irongate.entity.User
import com.bluelock.irongate.repository.UserRepository
import com.bluelock.irongate.security.JwtService
import com.bluelock.irongate.security.UserDetailsImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val authenticationManager: AuthenticationManager
) {
    @Value("\${jwt.access-token.expiration}")
    private var accessTokenExpiration: Long = 0

    fun register(request: RegisterRequest): AuthenticationResponse {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("El nombre de usuario ya está en uso")
        }

        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("El email ya está registrado")
        }

        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            role = request.role
        )

        val savedUser = userRepository.save(user)

        val jwtToken = jwtService.generateAccessToken(savedUser)
        val refreshToken = refreshTokenService.createRefreshToken(savedUser)

        return AuthenticationResponse(
            accessToken = jwtToken,
            refreshToken = refreshToken.token,
            expiresIn = accessTokenExpiration / 1000,
            user = UserResponse.fromUser(savedUser)
        )
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password
            )
        )

        val userDetails = authentication.principal as UserDetailsImpl
        val user = userDetails.getUser()

        val jwtToken = jwtService.generateAccessToken(user)
        val refreshToken = refreshTokenService.createRefreshToken(user)

        return AuthenticationResponse(
            accessToken = jwtToken,
            refreshToken = refreshToken.token,
            expiresIn = accessTokenExpiration / 1000,
            user = UserResponse.fromUser(user)
        )
    }

    fun refreshToken(request: TokenRefreshRequest): TokenRefreshResponse {
        val refreshToken = refreshTokenService.findByToken(request.refreshToken)
            ?: throw TokenRefreshException(request.refreshToken, "Refresh token no encontrado")

        refreshTokenService.verifyExpiration(refreshToken)

        val user = refreshToken.user
        val newAccessToken = jwtService.generateAccessToken(user)

        // Rotar el refresh token por seguridad
        val newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken)

        return TokenRefreshResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken.token,
            expiresIn = accessTokenExpiration / 1000
        )
    }

    fun logout(refreshToken: String) {
        refreshTokenService.revokeToken(refreshToken)
    }

    fun logoutAll(user: User) {
        refreshTokenService.revokeAllUserTokens(user)
    }
}