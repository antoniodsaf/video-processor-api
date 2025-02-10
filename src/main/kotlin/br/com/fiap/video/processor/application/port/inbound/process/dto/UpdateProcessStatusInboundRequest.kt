package br.com.fiap.video.processor.application.port.inbound.process.dto

data class UpdateProcessStatusInboundRequest(
    val id: String,
    val status: String
)