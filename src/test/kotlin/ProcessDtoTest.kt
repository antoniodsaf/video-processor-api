import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.adapter.inbound.entrypoint.controller.dto.ProcessDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProcessDtoTest {

    @Test
    fun `should create ProcessDto from Process successfully`() {
        val processId = ProcessId.generate()
        val process = Process.new(
            id = processId,
            originalFile = "video.mp4",
            status = ProcessStatus.RECEIVED,
            user = "testUser",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ).getOrThrow()

        val processDto = ProcessDto.from(process)

        assertEquals(process.id.value, processDto.id)
        assertEquals(process.originalFile, processDto.originalFile)
        assertEquals(process.user, processDto.user)
        assertEquals(process.status.value, processDto.status)
        assertEquals(process.createdAt, processDto.createdAt)
        assertEquals(process.updatedAt, processDto.updatedAt)
    }
}