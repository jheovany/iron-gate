package com.bluelock.irongate.security

import com.bluelock.irongate.entity.Role
import com.bluelock.irongate.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Implementación de UserDetails que encapsula una entidad User
 * Separando así la lógica de dominio de los detalles de Spring Security
 */
class UserDetailsImpl(
    private val user: User
) : UserDetails {
    
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
    }
    
    override fun getPassword(): String = user.password
    
    override fun getUsername(): String = user.email
    
    override fun isAccountNonExpired(): Boolean = user.isAccountNonExpired
    
    override fun isAccountNonLocked(): Boolean = user.isAccountNonLocked
    
    override fun isCredentialsNonExpired(): Boolean = user.isCredentialsNonExpired
    
    override fun isEnabled(): Boolean = user.isEnabled
    
    fun getUser(): User = user
}