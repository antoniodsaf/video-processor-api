package br.com.fiap.video.processor.application.port.inbound.process

import br.com.fiap.video.processor.application.port.inbound.process.dto.UpdateProcessStatusInboundRequest

interface UpdateProcessStatusService {
    fun invoke(updateProcessStatusInboundRequest: UpdateProcessStatusInboundRequest)
}
