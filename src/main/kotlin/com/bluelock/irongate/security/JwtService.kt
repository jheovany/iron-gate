package com.bluelock.irongate.security

import com.bluelock.irongate.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import javax.crypto.SecretKey

@Service
class JwtService {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    @Value("\${jwt.access-token.expiration}")
    private var accessTokenExpiration: Long = 0

    @Value("\${jwt.refresh-token.expiration}")
    private var refreshTokenExpiration: Long = 0

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    fun generateAccessToken(user: User): String {
        return generateAccessToken(HashMap(), user)
    }

    fun generateAccessToken(extraClaims: Map<String, Any>, user: User): String {
        return buildToken(extraClaims, user, accessTokenExpiration)
    }

    fun generateRefreshToken(user: User): String {
        return buildToken(HashMap(), user, refreshTokenExpiration)
    }

    private fun buildToken(
        extraClaims: Map<String, Any>,
        user: User,
        expiration: Long
    ): String {
        return Jwts.builder()
            .subject(user.username)
            .claim("userId", user.id)
            .claim("email", user.email)
            .claim("role", user.role.name)
            .claims(extraClaims)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey())
            .compact()
    }

//    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
    fun isTokenValid(token: String): Boolean {
//        val username = extractUsername(token)
//        return (username == userDetails.username) && !isTokenExpired(token)
        return !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

     fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun getSignInKey(): SecretKey {
        val keyBytes = Base64.getDecoder().decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun extractUserIdFromToken(token: String): Long {
        return extractClaim(token) { claims: Claims ->
            claims["userId"] as Long
        }
    }

    fun extractRoleFromToken(token: String): String {
        return extractClaim(token) { claims: Claims ->
            claims["role"] as String
        }
    }
}