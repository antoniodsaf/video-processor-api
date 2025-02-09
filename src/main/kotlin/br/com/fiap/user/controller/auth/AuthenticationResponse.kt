package br.com.fiap.user.controller.auth

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)
