package com.bluelock.irongate.service

class TokenRefreshException(
    token: String,
    message: String
) : RuntimeException("Error al refrescar token [$token]: $message")