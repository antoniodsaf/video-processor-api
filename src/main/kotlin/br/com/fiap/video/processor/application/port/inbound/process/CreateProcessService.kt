package br.com.fiap.video.processor.application.port.inbound.process

import br.com.fiap.video.processor.application.port.inbound.process.dto.CreateProcessInboundRequest

interface CreateProcessService {
    operator fun invoke(createProcessInboundRequest: CreateProcessInboundRequest): Result<br.com.fiap.video.processor.application.core.domain.Process>
}
