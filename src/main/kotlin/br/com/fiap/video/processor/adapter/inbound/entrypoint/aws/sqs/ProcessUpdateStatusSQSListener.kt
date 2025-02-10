package br.com.fiap.video.processor.adapter.inbound.entrypoint.aws.sqs

import br.com.fiap.video.processor.application.port.inbound.process.UpdateProcessStatusService
import br.com.fiap.video.processor.application.port.inbound.process.dto.UpdateProcessStatusInboundRequest
import br.com.fiap.video.processor.application.port.outbound.process.dto.UpdateProcessMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class ProcessUpdateStatusSQSListener(
    private val updateProcessStatusService: UpdateProcessStatusService
) {

    @SqsListener("\${spring.cloud.aws.sqs.update-process-status-queue-name}")
    fun receiveMessage(message: String) {
        val processMessage = jacksonObjectMapper()
            .readValue(message, UpdateProcessMessage::class.java)
        updateProcessStatusService.invoke(
            UpdateProcessStatusInboundRequest(
                processMessage.id,
                processMessage.status.value
            )
        )
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ProcessUpdateStatusSQSListener::class.java)
    }


}

