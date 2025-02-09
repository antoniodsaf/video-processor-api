package br.com.fiap.user.controller.auth

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
