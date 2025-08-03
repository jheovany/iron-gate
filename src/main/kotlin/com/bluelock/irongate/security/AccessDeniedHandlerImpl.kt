package com.bluelock.irongate.security

import com.bluelock.irongate.dto.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AccessDeniedHandlerImpl(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: org.springframework.security.access.AccessDeniedException?
    ) {
        response?.status = HttpServletResponse.SC_FORBIDDEN
        response?.contentType = MediaType.APPLICATION_JSON_VALUE

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpServletResponse.SC_FORBIDDEN,
            error = "Forbidden",
            message = "Do not have permission to access this resource",
            path = request?.requestURI ?: "Unknown Path"
        )

        response?.writer?.write(objectMapper.writeValueAsString(errorResponse))
    }
}