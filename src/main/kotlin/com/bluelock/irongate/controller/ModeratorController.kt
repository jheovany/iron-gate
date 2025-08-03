package com.bluelock.irongate.controller

import com.bluelock.irongate.dto.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/moderator")
@SecurityRequirement(name = "BearerAuth")
class ModeratorController {

    @GetMapping("/reports")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    fun getReports(): ResponseEntity<ApiResponse<Map<String, String>>> {
        val data = mapOf(
            "message" to "Reportes de moderación",
            "access_level" to "MODERATOR or ADMIN"
        )

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "Reportes obtenidos (MODERATOR/ADMIN)",
                data = data
            )
        )
    }
}