package com.bluelock.irongate.controller

import com.bluelock.irongate.dto.ApiResponse
import com.bluelock.irongate.dto.UserResponse
import com.bluelock.irongate.entity.Role
import com.bluelock.irongate.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// TODO: Verify total functionality of this controller
@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val userService: UserService
) {
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val pageable = PageRequest.of(page, size)
        val usersPage = userService.findAll(pageable)

        val usersResponse = usersPage.content.map { user ->
            UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                role = user.role,
                isEnabled = user.isEnabled
            )
        }

        val data = mapOf(
            "users" to usersResponse,
            "totalElements" to usersPage.totalElements,
            "totalPages" to usersPage.totalPages,
            "currentPage" to usersPage.number,
            "size" to usersPage.size
        )

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Lista de usuarios obtenida exitosamente",
                data = data
            )
        )
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.findById(id)
        val userResponse = UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role,
            isEnabled = user.isEnabled
        )

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Usuario encontrado",
                data = userResponse
            )
        )
    }

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUserRole(
        @PathVariable id: Long,
        @RequestParam role: String
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val newRole = try {
            Role.valueOf(role.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Rol inválido: $role")
        }

        val updatedUser = userService.updateUserRole(id, newRole)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Rol de usuario actualizado exitosamente",
                data = updatedUser
            )
        )
    }

    @PutMapping("/users/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    fun enableUser(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val updatedUser = userService.enableUser(id)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Usuario habilitado exitosamente",
                data = updatedUser
            )
        )
    }

    @PutMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    fun disableUser(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val updatedUser = userService.disableUser(id)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Usuario deshabilitado exitosamente",
                data = updatedUser
            )
        )
    }
}