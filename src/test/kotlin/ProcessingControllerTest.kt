import br.com.fiap.video.processor.adapter.inbound.entrypoint.controller.ProcessingController
import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.port.inbound.process.CreateProcessService
import br.com.fiap.video.processor.application.port.inbound.process.DownloadFramesZipService
import br.com.fiap.video.processor.application.port.inbound.process.GetProcessByUserService
import br.com.fiap.video.processor.application.port.inbound.process.dto.CreateProcessInboundRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.time.LocalDateTime

class ProcessingControllerTest {

    private lateinit var createProcessService: CreateProcessService
    private lateinit var downloadFramesZipService: DownloadFramesZipService
    private lateinit var getProcessByUserService: GetProcessByUserService
    private lateinit var processingController: ProcessingController

    @BeforeEach
    fun setUp() {
        createProcessService = mockk()
        downloadFramesZipService = mockk()
        getProcessByUserService = mockk()
        processingController = ProcessingController(createProcessService, downloadFramesZipService, getProcessByUserService)
    }

    @Test
    fun `should create process successfully`() {
        val mockFile = MockMultipartFile("file", "video.mp4", "video/mp4", ByteArray(0))
        every { createProcessService.invoke(any()) } returns Process.new(ProcessId.generate(), "video.mp4", "fake-user", ProcessStatus.RECEIVED, LocalDateTime.now(), LocalDateTime.now())

        processingController.createProcess(mockFile)

        verify { createProcessService.invoke(any<CreateProcessInboundRequest>()) }
    }

    @Test
    fun `should get process by user successfully`() {
        val processList = listOf(Process.new(ProcessId.generate(), "video.mp4", "fake-user", ProcessStatus.RECEIVED, LocalDateTime.now(), LocalDateTime.now()).getOrThrow())
        every { getProcessByUserService.invoke() } returns Result.success(processList)

        val response = processingController.getProcessByUser()

        assertTrue(response.statusCode == HttpStatus.OK)
        assertTrue(response.body is List<*>)
        assertTrue((response.body?.size ?: 0) == processList.size)
        verify { getProcessByUserService.invoke() }
    }

    @Test
    fun `should download frames zip successfully`() {
        val processId = "validProcessId"
        val inputStreamResource = mockk<InputStreamResource>()
        every { downloadFramesZipService.invoke(processId) } returns Result.success(inputStreamResource)

        val response = processingController.download(processId)

        assertTrue(response.statusCode == HttpStatus.OK)
        assertTrue(response.headers[HttpHeaders.CONTENT_DISPOSITION]?.contains("attachment; filename=$processId.zip") == true)
        assertTrue(response.headers[HttpHeaders.CONTENT_TYPE]?.contains(MediaType.APPLICATION_OCTET_STREAM_VALUE) == true)
        assertTrue(response.body == inputStreamResource)
        verify { downloadFramesZipService.invoke(processId) }
    }
}