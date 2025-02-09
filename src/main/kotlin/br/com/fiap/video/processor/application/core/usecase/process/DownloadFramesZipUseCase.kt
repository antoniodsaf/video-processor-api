package br.com.fiap.video.processor.application.core.usecase.process

import br.com.fiap.user.config.UserUtil
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.usecase.process.exception.InvalidProcessStateException
import br.com.fiap.video.processor.application.core.usecase.process.exception.ProcessNotFoundException
import br.com.fiap.video.processor.application.port.inbound.process.DownloadFramesZipService
import br.com.fiap.video.processor.application.port.outbound.process.FileDownloader
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.util.EXTENSION_ZIP
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Component

@Component
class DownloadFramesZipUseCase(
    private val downloader: FileDownloader,
    private val processRepository: ProcessRepository,
) : DownloadFramesZipService {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DownloadFramesZipUseCase::class.java)
    }

    override fun invoke(key: String): Result<InputStreamResource> {
        val process = processRepository.findById(
            ProcessId.new(key)
                .getOrElse { return Result.failure(InvalidProcessStateException("invalid process id.")) })

        if (process == null || process.user != UserUtil.authenticatedUsername) {
            return Result.failure(ProcessNotFoundException("process $key not found."))
        }
        logger.info("Downloading frames zip: $key")
        val inputStream = InputStreamResource(runCatching { downloader.download("${key}.${EXTENSION_ZIP}") }
            .getOrElse { return Result.failure(InvalidProcessStateException("invalid download id.")) })

        return Result.success(inputStream)
    }

}
