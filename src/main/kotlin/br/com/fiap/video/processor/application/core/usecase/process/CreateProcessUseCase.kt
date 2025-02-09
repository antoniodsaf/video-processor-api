package br.com.fiap.video.processor.application.core.usecase.process

import br.com.fiap.user.config.UserUtil
import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.core.usecase.process.exception.InvalidProcessStateException
import br.com.fiap.video.processor.application.mapper.process.ProcessMapper
import br.com.fiap.video.processor.application.port.inbound.process.CreateProcessService
import br.com.fiap.video.processor.application.port.inbound.process.dto.CreateProcessInboundRequest
import br.com.fiap.video.processor.application.port.outbound.process.FileUploader
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.ProcessSentMessenger
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessMessage
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import br.com.fiap.video.processor.util.flatMap
import br.com.fiap.video.processor.util.mapFailure
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

class CreateProcessUseCase(
    private val processMapper: ProcessMapper,
    private val processRepository: ProcessRepository,
    private val processSentMessenger: ProcessSentMessenger,
    private val uploader: FileUploader
) : CreateProcessService {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CreateProcessUseCase::class.java)
    }

    override fun invoke(createProcessInboundRequest: CreateProcessInboundRequest): Result<Process> {
        val result = createProcessInboundRequest
            .validateAndInstance()

        result.getOrElse { return Result.failure(InvalidProcessStateException("process is invalid.")) }
            .let {
                logger.info("Processing video: ${createProcessInboundRequest.video.originalFilename}")
                uploader.upload(createProcessInboundRequest.video, it.id.value)
                processSentMessenger.send(ProcessMessage(it.id.value, it.user, it.originalFile))
            }
        return result.save()
    }


    private fun CreateProcessInboundRequest.toProcess(status: ProcessStatus): Result<Process> {
        return processMapper.new(
            id = ProcessId.generate(),
            originalFile = video.originalFilename ?: "",
            status = status,
            user = UserUtil.authenticatedUsername.orEmpty(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
    }

    private fun CreateProcessInboundRequest.validateAndInstance(): Result<Process> {
        if (isVideoFile(video).not()) {
            return Result.failure(InvalidProcessStateException("video is invalid."))
        }
        if (UserUtil.authenticatedUsername == null) {
            return Result.failure(InvalidProcessStateException("user is invalid."))
        }

        return toProcess(ProcessStatus.RECEIVED)
    }

    private fun isVideoFile(file: MultipartFile): Boolean {
        if (file.isEmpty) {
            return false
        }

        val videoMimeTypes = listOf(
            "video/mp4",
            "video/mpeg",
            "video/ogg",
            "video/quicktime",
            "video/webm",
            "video/x-ms-wmv",
            "video/x-flv",
            "video/3gpp",
            "video/3gpp2",
            "video/x-matroska"
        )
        return videoMimeTypes.contains(file.contentType)
    }

    private fun Result<Process>.save(): Result<Process> {
        return flatMap {
            processRepository.save(it)
                .toProcess(it.status)
                .mapFailure { InvalidProcessStateException("process in invalid state.") }
        }
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
