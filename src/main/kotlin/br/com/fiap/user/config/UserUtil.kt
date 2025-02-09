package br.com.fiap.user.config

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

object UserUtil {
    val authenticatedUsername: String?
        get() {
            val authentication: Authentication? = SecurityContextHolder.getContext().authentication
            if (authentication != null) {
                val principal: Any = authentication.getPrincipal()
                return if (principal is UserDetails) {
                    principal.username
                } else {
                    principal.toString()
                }
            }
            return null
        }
}