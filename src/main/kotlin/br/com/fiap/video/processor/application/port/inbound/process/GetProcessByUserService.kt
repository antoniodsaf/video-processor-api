package br.com.fiap.video.processor.application.port.inbound.process

fun interface GetProcessByUserService {
    operator fun invoke(): Result<List<br.com.fiap.video.processor.application.core.domain.Process>>
}
