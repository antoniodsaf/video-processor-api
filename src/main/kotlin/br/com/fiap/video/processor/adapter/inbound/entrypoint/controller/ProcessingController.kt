package br.com.fiap.video.processor.adapter.inbound.entrypoint.controller

import br.com.fiap.user.config.UserUtil
import br.com.fiap.video.processor.adapter.inbound.entrypoint.controller.dto.ProcessDto
import br.com.fiap.video.processor.adapter.outbound.aws.s3.client.S3FileTransfer
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.port.inbound.process.CreateProcessService
import br.com.fiap.video.processor.application.port.inbound.process.DownloadFramesZipService
import br.com.fiap.video.processor.application.port.inbound.process.GetProcessByUserService
import br.com.fiap.video.processor.application.port.inbound.process.dto.CreateProcessInboundRequest
import br.com.fiap.video.processor.application.port.outbound.process.FileDownloader
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/processing")
class ProcessingController(
    private val createProcessService: CreateProcessService,
    private val downloadFramesZipService: DownloadFramesZipService,
    private val getProcessByUserService: GetProcessByUserService,
) {

    @PostMapping
    fun createProcess(@RequestPart(value = "file", required = true) multipartFile: MultipartFile) {
        createProcessService.invoke(CreateProcessInboundRequest(multipartFile))
    }

    @GetMapping
    fun getProcessByUser(): ResponseEntity<List<ProcessDto>> {
        return getProcessByUserService.invoke()
            .map { it.map { process -> ProcessDto.from(process) } }
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
            .getOrThrow()
    }

    @GetMapping("/{id}")
    fun download(@PathVariable("id") id: String): ResponseEntity<InputStreamResource> {
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$id.zip")
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)

        return ResponseEntity.ok()
            .headers(headers)
            .body(downloadFramesZipService.invoke(id).getOrThrow())
    }
}
