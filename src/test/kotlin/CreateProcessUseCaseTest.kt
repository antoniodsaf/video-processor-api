import br.com.fiap.user.config.UserUtil
import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.core.usecase.process.CreateProcessUseCase
import br.com.fiap.video.processor.application.core.usecase.process.exception.InvalidProcessStateException
import br.com.fiap.video.processor.application.mapper.process.ProcessMapper
import br.com.fiap.video.processor.application.port.inbound.process.dto.CreateProcessInboundRequest
import br.com.fiap.video.processor.application.port.outbound.process.FileUploader
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.ProcessSentMessenger
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime

class CreateProcessUseCaseTest {

    private lateinit var processMapper: ProcessMapper
    private lateinit var processRepository: ProcessRepository
    private lateinit var processSentMessenger: ProcessSentMessenger
    private lateinit var uploader: FileUploader
    private lateinit var createProcessUseCase: CreateProcessUseCase

    @BeforeEach
    fun setUp() {
        processMapper = mockk()
        processRepository = mockk()
        processSentMessenger = mockk()
        uploader = mockk()
        createProcessUseCase = CreateProcessUseCase(processMapper, processRepository, processSentMessenger, uploader)
        mockkObject(UserUtil)
    }

    @Test
    fun `should create process successfully`() {
        val username = "testUser"
        val mockFile = mockk<MultipartFile>()
        every { mockFile.originalFilename } returns "samplevideo.mp4"
        every { mockFile.contentType } returns "video/mp4"
        every { mockFile.isEmpty } returns false
        every { mockFile.inputStream } returns File("src/test/resources/samplevideo.mp4").inputStream()

        val request = CreateProcessInboundRequest(mockFile)
        val process = Process.new(
            id = ProcessId.generate(),
            originalFile = "samplevideo.mp4",
            status = ProcessStatus.RECEIVED,
            user = username,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ).getOrThrow()

        every { processMapper.new(any(), any(), any(), any(), any(), any()) } returns Result.success(process)
        every { processRepository.save(any()) } returns ProcessOutboundResponse(
            process.id.value,
            process.originalFile,
            process.user,
            process.status.value,
            process.createdAt,
            process.updatedAt
        )
        every { uploader.upload(any(), any()) } returns Unit
        every { processSentMessenger.send(any()) } returns Unit
        every { UserUtil.authenticatedUsername } returns username

        val result = createProcessUseCase.invoke(request)

        assertTrue(result.isSuccess)
        assertEquals(process.id, result.getOrNull()?.id)
        verify { uploader.upload(mockFile, process.id.value) }
        verify { processSentMessenger.send(any()) }
    }

    @Test
    fun `should fail when video is invalid`() {
        val mockFile = mockk<MultipartFile>()
        every { mockFile.isEmpty } returns true

        val request = CreateProcessInboundRequest(mockFile)

        val result = createProcessUseCase.invoke(request)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidProcessStateException)
    }
}