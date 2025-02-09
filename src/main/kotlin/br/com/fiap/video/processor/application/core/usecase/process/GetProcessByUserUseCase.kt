package br.com.fiap.video.processor.application.core.usecase.process

import br.com.fiap.user.config.UserUtil
import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.core.usecase.process.exception.InvalidProcessStateException
import br.com.fiap.video.processor.application.mapper.process.ProcessMapper
import br.com.fiap.video.processor.application.port.inbound.process.GetProcessByUserService
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GetProcessByUserUseCase(
    private val processMapper: ProcessMapper,
    private val processRepository: ProcessRepository,
) : GetProcessByUserService {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GetProcessByUserUseCase::class.java)
    }

    override fun invoke(): Result<List<br.com.fiap.video.processor.application.core.domain.Process>> {
        val entries = processRepository.findByUser(UserUtil.authenticatedUsername.orEmpty())
            .map {
                it.toProcess().getOrElse { return Result.failure(InvalidProcessStateException("process is invalid.")) }
            }

        return Result.success(entries)
    }

    private fun ProcessOutboundResponse?.toProcess(): Result<Process> {
        return if (this != null)
            processMapper.new(
                ProcessId.new(this.id).getOrThrow(),
                this.originalFile,
                this.user,
                ProcessStatus.findByValue(this.status).getOrElse { return Result.failure(it) },
                this.createdAt, this.updatedAt
            )
        else
            Result.failure(InvalidProcessStateException("process in invalid state."))
    }
}
