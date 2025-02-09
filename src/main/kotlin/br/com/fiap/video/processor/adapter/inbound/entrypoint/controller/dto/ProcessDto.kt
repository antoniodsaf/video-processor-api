package br.com.fiap.video.processor.adapter.inbound.entrypoint.controller.dto

import br.com.fiap.video.processor.application.core.domain.Process
import java.time.LocalDateTime

data class ProcessDto(
    val id: String,
    val originalFile: String,
    val user: String,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
companion object {
    fun from(process: Process): ProcessDto = with(process) {
        ProcessDto(
            id.value,
            originalFile,
            user,
            status.value,
            createdAt,
            updatedAt,
        )
    }
}
}