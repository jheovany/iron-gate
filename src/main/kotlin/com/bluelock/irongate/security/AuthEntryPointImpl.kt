package com.bluelock.irongate.security

import com.bluelock.irongate.dto.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AuthEntryPointImpl(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpServletResponse.SC_UNAUTHORIZED,
            error = "Unauthorized",
            message = "Do not have permission to access this resource",
            path = request.requestURI
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
