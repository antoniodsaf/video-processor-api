package br.com.fiap.user.repo.jpa

import br.com.fiap.user.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, String> {
    fun findByEmail(email: String): UserEntity?
}