import br.com.fiap.user.config.UserUtil
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.usecase.process.DownloadFramesZipUseCase
import br.com.fiap.video.processor.application.core.usecase.process.exception.InvalidProcessStateException
import br.com.fiap.video.processor.application.core.usecase.process.exception.ProcessNotFoundException
import br.com.fiap.video.processor.application.port.outbound.process.FileDownloader
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.InputStreamResource
import java.io.InputStream
import java.time.LocalDateTime

class DownloadFramesZipUseCaseTest {

    private lateinit var downloader: FileDownloader
    private lateinit var processRepository: ProcessRepository
    private lateinit var downloadFramesZipUseCase: DownloadFramesZipUseCase

    @BeforeEach
    fun setUp() {
        downloader = mockk()
        processRepository = mockk()
        downloadFramesZipUseCase = DownloadFramesZipUseCase(downloader, processRepository)
        mockkObject(UserUtil)
    }

    @Test
    fun `should download frames zip successfully`() {
        val processId = ProcessId.generate()
        val username = "testUser"
        val processOutboundResponse = ProcessOutboundResponse(
            id = processId.value,
            originalFile = "file.mp4",
            user = username,
            status = "DONE",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { processRepository.findById(any()) } returns processOutboundResponse
        every { UserUtil.authenticatedUsername } returns username
        every { downloader.download(any()) } returns mockk<InputStream>()

        val result = downloadFramesZipUseCase.invoke(processId.value)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() is InputStreamResource)
        verify { downloader.download("${processId.value}.zip") }
    }

    @Test
    fun `should fail when process not found`() {
        val processId = ProcessId.generate().value
        val username = "testUser"

        every { processRepository.findById(any()) } returns null
        every { UserUtil.authenticatedUsername } returns username

        val result = downloadFramesZipUseCase.invoke(processId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ProcessNotFoundException)
    }

    @Test
    fun `should fail when download is invalid`() {
        val processId = ProcessId.generate()
        val username = "testUser"
        val processOutboundResponse = ProcessOutboundResponse(
            id = processId.value,
            originalFile = "file.mp4",
            user = username,
            status = "DONE",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { processRepository.findById(any()) } returns processOutboundResponse
        every { UserUtil.authenticatedUsername } returns username
        every { downloader.download(any()) } throws RuntimeException()

        val result = downloadFramesZipUseCase.invoke(processId.value)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidProcessStateException)
    }
}