package br.com.fiap.user.repo

import br.com.fiap.user.model.dto.User
import br.com.fiap.user.model.UserEntity
import br.com.fiap.user.model.toUser
import br.com.fiap.user.repo.jpa.UserJpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.*

open class UserRepositoryImpl(
    private val encoder: PasswordEncoder,
    private val userJpaRepository: UserJpaRepository
): UserRepository {
    /*private val users = mutableSetOf(
        User(
            id = UUID.randomUUID(),
            email = "email-1@gmail.com",
            password = encoder.encode("pass1"),
            role = Role.USER,
        ),
        User(
            id = UUID.randomUUID(),
            email = "email-2@gmail.com",
            password = encoder.encode("pass2"),
            role = Role.ADMIN,
        ),
        User(
            id = UUID.randomUUID(),
            email = "email-3@gmail.com",
            password = encoder.encode("pass3"),
            role = Role.USER,
        ),
    )*/

    override fun save(user: User) {
        val updated = user.copy(password = encoder.encode(user.password))

        userJpaRepository.save(
            UserEntity(
                updated.id.toString(),
                updated.email,
                updated.password,
                updated.role.name,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        )
    }

    override fun findByEmail(email: String): User? = userJpaRepository.findByEmail(email)?.toUser()

    override fun findAll(): Set<User> =
        userJpaRepository.findAll().map { it.toUser() }.toSet()

    override fun findByUUID(uuid: UUID): User? = userJpaRepository.findById(uuid.toString()).map { it.toUser() }.orElse(null)

    override fun deleteByUUID(uuid: UUID) {
        userJpaRepository.deleteById(uuid.toString())
    }

    override fun existsByUUID(uuid: UUID): Boolean {
        return userJpaRepository.existsById(uuid.toString())
    }
}
