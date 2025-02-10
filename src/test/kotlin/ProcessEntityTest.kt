import br.com.fiap.video.processor.adapter.outbound.database.entity.ProcessEntity
import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProcessEntityTest {

    @Test
    fun `should create ProcessEntity from Process successfully`() {
        val processId = ProcessId.generate()
        val process = Process.new(
            id = processId,
            originalFile = "video.mp4",
            status = ProcessStatus.RECEIVED,
            user = "testUser",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ).getOrThrow()

        val processEntity = ProcessEntity.from(process)

        assertEquals(process.id.value, processEntity.id)
        assertEquals(process.status.value, processEntity.status)
        assertEquals(process.originalFile, processEntity.originalFile)
        assertEquals(process.user, processEntity.user)
        assertEquals(process.createdAt, processEntity.createdAt)
        assertEquals(process.updatedAt, processEntity.updatedAt)
    }

    @Test
    fun `should convert ProcessEntity to ProcessOutboundResponse successfully`() {
        val processEntity = ProcessEntity(
            id = "processId",
            status = "RECEIVED",
            originalFile = "video.mp4",
            user = "testUser",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val processOutboundResponse = processEntity.toOutbound()

        assertEquals(processEntity.id, processOutboundResponse.id)
        assertEquals(processEntity.status, processOutboundResponse.status)
        assertEquals(processEntity.originalFile, processOutboundResponse.originalFile)
        assertEquals(processEntity.user, processOutboundResponse.user)
        assertEquals(processEntity.createdAt, processOutboundResponse.createdAt)
        assertEquals(processEntity.updatedAt, processOutboundResponse.updatedAt)
    }
}