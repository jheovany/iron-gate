package com.bluelock.irongate.service

import com.bluelock.irongate.dto.UserResponse
import com.bluelock.irongate.entity.Role
import com.bluelock.irongate.entity.User
import com.bluelock.irongate.repository.UserRepository
import com.bluelock.irongate.security.UserDetailsImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun findById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con ID: $id") }
    }
    
    fun findByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Usuario no encontrado con email: $email")
    }
    
    fun findAll(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }
    
    fun getCurrentUser(authentication: Authentication): User {
        val userDetails = authentication.principal as UserDetailsImpl
        return userDetails.getUser()
    }
    
    fun getCurrentUserResponse(authentication: Authentication): UserResponse {
        val user = getCurrentUser(authentication)
        return UserResponse(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role,
            isEnabled = user.isEnabled
        )
    }
    
    @Transactional
    fun updateProfile(
        authentication: Authentication,
        firstName: String? = null,
        lastName: String? = null
    ): UserResponse {
        val currentUser = getCurrentUser(authentication)
        
        val updatedUser = currentUser.copy(
            firstName = firstName ?: currentUser.firstName,
            lastName = lastName ?: currentUser.lastName
        )
        
        val savedUser = userRepository.save(updatedUser)
        
        return UserResponse(
            id = savedUser.id,
            email = savedUser.email,
            firstName = savedUser.firstName,
            lastName = savedUser.lastName,
            role = savedUser.role,
            isEnabled = savedUser.isEnabled
        )
    }
    
    @Transactional
    fun changePassword(
        authentication: Authentication,
        currentPassword: String,
        newPassword: String
    ) {
        val currentUser = getCurrentUser(authentication)
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, currentUser.password)) {
            throw IllegalArgumentException("Contraseña actual incorrecta")
        }
        
        // Actualizar con nueva contraseña
        val updatedUser = currentUser.copy(
            password = passwordEncoder.encode(newPassword),
            isCredentialsNonExpired = true
        )
        
        userRepository.save(updatedUser)
    }
    
    @Transactional
    fun updateUserRole(userId: Long, newRole: Role): UserResponse {
        val user = findById(userId)
        val updatedUser = user.copy(role = newRole)
        val savedUser = userRepository.save(updatedUser)
        
        return UserResponse(
            id = savedUser.id,
            email = savedUser.email,
            firstName = savedUser.firstName,
            lastName = savedUser.lastName,
            role = savedUser.role,
            isEnabled = savedUser.isEnabled
        )
    }
    
    @Transactional
    fun enableUser(userId: Long): UserResponse {
        val user = findById(userId)
        val updatedUser = user.copy(isEnabled = true)
        val savedUser = userRepository.save(updatedUser)
        
        return UserResponse(
            id = savedUser.id,
            email = savedUser.email,
            firstName = savedUser.firstName,
            lastName = savedUser.lastName,
            role = savedUser.role,
            isEnabled = savedUser.isEnabled
        )
    }
    
    @Transactional
    fun disableUser(userId: Long): UserResponse {
        val user = findById(userId)
        val updatedUser = user.copy(isEnabled = false)
        val savedUser = userRepository.save(updatedUser)
        
        return UserResponse(
            id = savedUser.id,
            email = savedUser.email,
            firstName = savedUser.firstName,
            lastName = savedUser.lastName,
            role = savedUser.role,
            isEnabled = savedUser.isEnabled
        )
    }
    
    @Transactional
    fun deleteUser(userId: Long) {
        val user = findById(userId)
        userRepository.delete(user)
    }
    
    fun getUsersByRole(role: Role, pageable: Pageable): Page<User> {
        return userRepository.findByRole(role, pageable)
    }
    
    fun searchUsersByEmail(emailPattern: String, pageable: Pageable): Page<User> {
        return userRepository.findByEmailContainingIgnoreCase(emailPattern, pageable)
    }
    
    fun getUserStatistics(): Map<String, Any> {
        val totalUsers = userRepository.count()
        val enabledUsers = userRepository.countByIsEnabled(true)
        val disabledUsers = userRepository.countByIsEnabled(false)
        val usersByRole = Role.values().associate { role ->
            role.name to userRepository.countByRole(role)
        }
        
        return mapOf(
            "totalUsers" to totalUsers,
            "enabledUsers" to enabledUsers,
            "disabledUsers" to disabledUsers,
            "usersByRole" to usersByRole
        )
    }
}