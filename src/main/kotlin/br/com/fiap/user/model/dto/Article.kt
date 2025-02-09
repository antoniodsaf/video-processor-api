package br.com.fiap.user.model.dto

import java.util.*

data class Article(
    val id: UUID,
    val title: String,
    val content: String,
)
