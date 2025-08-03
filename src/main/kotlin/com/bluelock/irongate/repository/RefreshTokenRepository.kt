package com.bluelock.irongate.repository

import com.bluelock.irongate.entity.RefreshToken
import com.bluelock.irongate.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    fun deleteByUser(user: User)

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    fun deleteExpiredTokens(now: LocalDateTime)

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.token = :token")
    fun revokeToken(token: String)

    fun findByUserAndIsRevokedFalse(user: User): List<RefreshToken>
}
