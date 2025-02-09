package br.com.fiap.video.processor.application.port.inbound.process.dto

import org.springframework.web.multipart.MultipartFile

data class CreateProcessInboundRequest(
    val video: MultipartFile,
)