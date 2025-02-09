package br.com.fiap.user.config

import br.com.fiap.user.repo.jpa.UserJpaRepository
import br.com.fiap.user.repo.UserRepository
import br.com.fiap.user.repo.UserRepositoryImpl
import br.com.fiap.user.service.CustomUserDetailsService
import jakarta.transaction.Transactional
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.RepositoryDefinition
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(br.com.fiap.user.config.JwtProperties::class)
@EnableJpaRepositories("br.com.fiap.user.repo.jpa")
@EntityScan("br.com.fiap.user.model")
class Configuration {
    @Bean
    fun userRepository(encoder: PasswordEncoder, repository: UserJpaRepository): UserRepository {
        return (@Transactional object : UserRepositoryImpl(encoder, repository) {})
    }

    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService =
        CustomUserDetailsService(userRepository)

    @Bean
    fun authenticationProvider(userRepository: UserRepository): AuthenticationProvider =
        DaoAuthenticationProvider()
            .also {
                it.setUserDetailsService(userDetailsService(userRepository))
                it.setPasswordEncoder(encoder())
            }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()
}
