package br.com.fiap.user.model

import br.com.fiap.user.model.dto.Role
import br.com.fiap.user.model.dto.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@Entity(name = "usuario")
data class UserEntity(
    @Id
    @Column(name = "id", length = 36)
    val id: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "senha")
    val password: String,

    @Column(name = "role")
    val role: String,

    @Column(name = "criado_em")
    val createdAt: LocalDateTime,

    @Column(name = "atualizado_em")
    val updatedAt: LocalDateTime,
)

fun UserEntity.toUser(): User {
    return User(
        id = UUID.fromString(id),
        email = email,
        password = password,
        role = Role.valueOf(role)
    )
}