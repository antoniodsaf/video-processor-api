package br.com.fiap.video.processor.application.core.usecase.process

import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.core.usecase.exception.NotFoundException
import br.com.fiap.video.processor.application.core.usecase.process.exception.InvalidProcessStateException
import br.com.fiap.video.processor.application.core.usecase.process.exception.ProcessNotFoundException
import br.com.fiap.video.processor.application.mapper.process.ProcessMapper
import br.com.fiap.video.processor.application.port.inbound.process.UpdateProcessStatusService
import br.com.fiap.video.processor.application.port.inbound.process.dto.UpdateProcessStatusInboundRequest
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import br.com.fiap.video.processor.util.flatMap
import br.com.fiap.video.processor.util.mapFailure
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UpdateProcessStatusUseCase(
    private val processMapper: ProcessMapper,
    private val processRepository: ProcessRepository,
) : UpdateProcessStatusService {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UpdateProcessStatusUseCase::class.java)
    }

    override fun invoke(updateProcessStatusInboundRequest: UpdateProcessStatusInboundRequest) {
        logger.info("Updating process status of ${updateProcessStatusInboundRequest.id}")
        ProcessId.new(updateProcessStatusInboundRequest.id).getOrThrow().let { processId ->
            val processOutboundResponse = this.processRepository.findById(processId)
                ?: throw ProcessNotFoundException("process not found.")
            when (ProcessStatus.findByValue(updateProcessStatusInboundRequest.status)
                .getOrThrow()) {
                ProcessStatus.IN_PROGRESS -> {
                    processOutboundResponse.toInProgressProcess().save()
                }

                ProcessStatus.DONE -> {
                    processOutboundResponse.toDoneProcess().save()
                }

                ProcessStatus.ERROR -> {
                    processOutboundResponse.toErrorProcess().save()
                }

                else -> {
                    throw InvalidProcessStateException("process status is invalid.")
                }
            }
        }

    }

    private fun Result<Process>.save(): Result<Process> {
        return flatMap {
            processRepository.save(it)
                .toProcess(it.status)
                .mapFailure { InvalidProcessStateException("process in invalid state.") }
        }
    }

    private fun ProcessOutboundResponse?.toInProgressProcess(): Result<Process> {
        return this.toProcess(ProcessStatus.IN_PROGRESS)
    }

    private fun ProcessOutboundResponse?.toDoneProcess(): Result<Process> {
        return this.toProcess(ProcessStatus.DONE)
    }

    private fun ProcessOutboundResponse?.toErrorProcess(): Result<Process> {
        return this.toProcess(ProcessStatus.ERROR)
    }

    private fun ProcessOutboundResponse?.toProcess(status: ProcessStatus): Result<Process> {
        return if (this != null)
            processMapper.new(
                ProcessId.new(this.id).getOrThrow(),
                this.originalFile,
                this.user, status,
                this.createdAt, this.updatedAt
            )
        else
            Result.failure(InvalidProcessStateException("process in invalid state."))
    }
}
