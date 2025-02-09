package br.com.fiap.video.processor.adapter.outbound.database.entity

import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity(name = "processo")
data class ProcessEntity(
    @Id
    @Column(name = "id", length = 36)
    val id: String,

    @Column(name = "status", length = 50)
    val status: String,

    @Column(name = "arquivo_original")
    val originalFile: String,

    @Column(name = "usuario")
    val user: String,

    @Column(name = "criado_em")
    val createdAt: LocalDateTime,

    @Column(name = "atualizado_em")
    val updatedAt: LocalDateTime,
) {

    fun toOutbound(): ProcessOutboundResponse {
        return ProcessOutboundResponse(
            id = id,
            user = user,
            originalFile = originalFile,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    companion object {
        fun from(process: Process): ProcessEntity = process.run {
            ProcessEntity(
                id = id.value,
                status = status.value,
                originalFile = originalFile,
                user = user,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}