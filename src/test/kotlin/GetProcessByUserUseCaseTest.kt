import br.com.fiap.user.config.UserUtil
import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.exception.InvalidProcessStatusException
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.core.usecase.process.GetProcessByUserUseCase
import br.com.fiap.video.processor.application.core.usecase.process.exception.InvalidProcessStateException
import br.com.fiap.video.processor.application.mapper.process.ProcessMapper
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class GetProcessByUserUseCaseTest {

    private lateinit var processMapper: ProcessMapper
    private lateinit var processRepository: ProcessRepository
    private lateinit var getProcessByUserUseCase: GetProcessByUserUseCase

    @BeforeEach
    fun setUp() {
        processMapper = mockk()
        processRepository = mockk()
        getProcessByUserUseCase = GetProcessByUserUseCase(processMapper, processRepository)
        mockkObject(UserUtil)
    }

    @Test
    fun `should return processes when repository returns valid processes`() {
        val username = "testUser"
        val processId = ProcessId.generate()
        val processOutboundResponse = ProcessOutboundResponse(
            id = processId.value,
            originalFile = "file.mp4",
            user = username,
            status = "DONE",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val process = Process.new(
            id = processId,
            originalFile = "file.mp4",
            user = username,
            status = ProcessStatus.DONE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { UserUtil.authenticatedUsername } returns username
        every { processRepository.findByUser(username) } returns listOf(processOutboundResponse)
        every { processMapper.new(any(), any(), any(), any(), any(), any()) } returns process

        val result = getProcessByUserUseCase.invoke()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals(process.getOrThrow(), result.getOrThrow()[0])
    }

    @Test
    fun `should return failure when repository returns invalid process`() {
        val username = "testUser"
        val processOutboundResponse = ProcessOutboundResponse(
            id = ProcessId.generate().value,
            originalFile = "file.mp4",
            user = username,
            status = "INVALID_STATUS",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { UserUtil.authenticatedUsername } returns username
        every { processRepository.findByUser(username) } returns listOf(processOutboundResponse)
        every { processMapper.new(any(), any(), any(), any(), any(), any()) } returns Result.failure(InvalidProcessStateException("process is invalid."))

        val result = getProcessByUserUseCase.invoke()

        assertTrue(result.isFailure)
        assertThrows<InvalidProcessStateException> { result.getOrThrow() }
    }
}