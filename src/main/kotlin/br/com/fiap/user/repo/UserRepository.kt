package br.com.fiap.user.repo

import br.com.fiap.user.model.dto.User
import java.util.*

interface UserRepository {
    fun save(user: User)
    fun findByEmail(email: String): User?
    fun existsByUUID(uuid: UUID): Boolean
    fun deleteByUUID(uuid: UUID)
    fun findByUUID(uuid: UUID): User?
    fun findAll(): Set<User>
}
