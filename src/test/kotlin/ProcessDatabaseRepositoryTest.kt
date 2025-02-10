import br.com.fiap.video.processor.adapter.outbound.database.ProcessDatabaseRepository
import br.com.fiap.video.processor.adapter.outbound.database.entity.ProcessEntity
import br.com.fiap.video.processor.adapter.outbound.database.jpa.ProcessJpaRepository
import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class ProcessDatabaseRepositoryTest {

    private lateinit var repository: ProcessJpaRepository
    private lateinit var processDatabaseRepository: ProcessDatabaseRepository

    @BeforeEach
    fun setUp() {
        repository = mockk()
        processDatabaseRepository = ProcessDatabaseRepository(repository)
    }

    @Test
    fun `should save process successfully`() {
        val process = Process.new(
            id = ProcessId.generate(),
            originalFile = "video.mp4",
            status = ProcessStatus.RECEIVED,
            user = "testUser",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ).getOrThrow()
        val processEntity = ProcessEntity.from(process)
        val processOutboundResponse = processEntity.toOutbound()

        every { repository.save(any<ProcessEntity>()) } returns processEntity

        val result = processDatabaseRepository.save(process)

        assertEquals(processOutboundResponse, result)
        verify { repository.save(any<ProcessEntity>()) }
    }

    @Test
    fun `should find process by id successfully`() {
        val processId = ProcessId.generate()
        val processEntity = ProcessEntity(
            id = processId.value,
            status = "RECEIVED",
            originalFile = "video.mp4",
            user = "testUser",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val processOutboundResponse = processEntity.toOutbound()

        every { repository.findById(processId.value) } returns Optional.of(processEntity)

        val result = processDatabaseRepository.findById(processId)

        assertEquals(processOutboundResponse, result)
        verify { repository.findById(processId.value) }
    }

    @Test
    fun `should find processes by user successfully`() {
        val user = "testUser"
        val processEntity = ProcessEntity(
            id = "processId",
            status = "RECEIVED",
            originalFile = "video.mp4",
            user = user,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val processOutboundResponse = processEntity.toOutbound()
        val processEntityList = listOf(processEntity)

        every { repository.findByUser(user) } returns processEntityList

        val result = processDatabaseRepository.findByUser(user)

        assertEquals(listOf(processOutboundResponse), result)
        verify { repository.findByUser(user) }
    }
}