package com.bluelock.irongate.service

import com.bluelock.irongate.entity.RefreshToken
import com.bluelock.irongate.entity.User
import com.bluelock.irongate.repository.RefreshTokenRepository
import com.bluelock.irongate.security.JwtService
import com.bluelock.irongate.service.TokenRefreshException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService
) {
    @Value("\${jwt.refresh-token.expiration}")
    private var refreshTokenDurationMs: Long = 0

    fun createRefreshToken(user: User): RefreshToken {
        // Eliminar tokens existentes del usuario
        refreshTokenRepository.deleteByUser(user)

        val refreshToken = RefreshToken(
            token = jwtService.generateRefreshToken(user),
            user = user,
            expiryDate = LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000)
        )

        return refreshTokenRepository.save(refreshToken)
    }

    fun findByToken(token: String): RefreshToken? {
        return refreshTokenRepository.findByToken(token)
    }

    fun verifyExpiration(token: RefreshToken): RefreshToken {
        if (token.expiryDate.isBefore(LocalDateTime.now()) || token.isRevoked) {
            refreshTokenRepository.delete(token)
            throw TokenRefreshException(
                token.token,
                "Refresh token ha expirado o ha sido revocado. Por favor, inicie sesión nuevamente."
            )
        }
        return token
    }

    fun revokeToken(token: String) {
        refreshTokenRepository.revokeToken(token)
    }

    fun revokeAllUserTokens(user: User) {
        refreshTokenRepository.deleteByUser(user)
    }

    fun deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now())
    }

    fun rotateRefreshToken(oldToken: RefreshToken): RefreshToken {
        // Revocar el token anterior
        refreshTokenRepository.delete(oldToken)

        // Crear un nuevo token
        return createRefreshToken(oldToken.user)
    }
}