import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.core.usecase.process.UpdateProcessStatusUseCase
import br.com.fiap.video.processor.application.core.usecase.process.exception.ProcessNotFoundException
import br.com.fiap.video.processor.application.mapper.process.ProcessMapper
import br.com.fiap.video.processor.application.port.inbound.process.dto.UpdateProcessStatusInboundRequest
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UpdateProcessStatusUseCaseTest {

    private lateinit var processMapper: ProcessMapper
    private lateinit var processRepository: ProcessRepository
    private lateinit var updateProcessStatusUseCase: UpdateProcessStatusUseCase

    @BeforeEach
    fun setUp() {
        processMapper = mockk()
        processRepository = mockk()
        updateProcessStatusUseCase = UpdateProcessStatusUseCase(processMapper, processRepository)
    }

    @Test
    fun `should update process status to IN_PROGRESS successfully`() {
        val processId = ProcessId.generate()
        val process = Process.new(
            id = processId,
            originalFile = "video.mp4",
            status = ProcessStatus.RECEIVED,
            user = "testUser",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ).getOrThrow()
        val processOutboundResponse = ProcessOutboundResponse(
            process.id.value,
            process.originalFile,
            process.user,
            process.status.value,
            process.createdAt,
            process.updatedAt
        )

        every { processRepository.findById(processId) } returns processOutboundResponse
        every { processMapper.new(any(), any(), any(), any(), any(), any()) } returns Result.success(process)
        every { processRepository.save(any()) } returns processOutboundResponse

        val request = UpdateProcessStatusInboundRequest(processId.value, "IN_PROGRESS")
        updateProcessStatusUseCase.invoke(request)

        verify { processRepository.save(any()) }
    }

    @Test
    fun `should throw InvalidProcessStateException when process status is invalid`() {
        val processId = ProcessId.generate()
        val process = Process.new(
            id = processId,
            originalFile = "video.mp4",
            status = ProcessStatus.RECEIVED,
            user = "testUser",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ).getOrThrow()
        val processOutboundResponse = ProcessOutboundResponse(
            process.id.value,
            process.originalFile,
            process.user,
            process.status.value,
            process.createdAt,
            process.updatedAt
        )

        every { processRepository.findById(processId) } returns processOutboundResponse
        val request = UpdateProcessStatusInboundRequest(processId.value, "XPTO")

        assertThrows(br.com.fiap.video.processor.application.core.domain.exception.InvalidProcessStatusException::class.java) {
            updateProcessStatusUseCase.invoke(request)
        }
    }

    @Test
    fun `should throw InvalidProcessStateException when process is not found`() {
        val processId = ProcessId.generate()
        every { processRepository.findById(processId) } returns null

        val request = UpdateProcessStatusInboundRequest(processId.value, "IN_PROGRESS")

        assertThrows(ProcessNotFoundException::class.java) {
            updateProcessStatusUseCase.invoke(request)
        }
    }
}