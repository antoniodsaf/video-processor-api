package br.com.fiap.video.processor.application.port.inbound.process

import org.springframework.core.io.InputStreamResource

interface DownloadFramesZipService {
    operator fun invoke(key: String): Result<InputStreamResource>
}
