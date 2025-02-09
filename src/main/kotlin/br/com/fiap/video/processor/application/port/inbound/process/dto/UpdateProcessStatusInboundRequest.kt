package br.com.fiap.video.processor.application.port.inbound.process.dto

import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus

data class UpdateProcessStatusInboundRequest(
    val id: String,
    val status: ProcessStatus
)