import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessStatus
import br.com.fiap.video.processor.application.port.outbound.process.dto.UpdateProcessMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateProcessMessageTest {

    @Test
    fun `should create UpdateProcessMessage successfully`() {
        val id = "processId"
        val status = ProcessStatus.RECEIVED

        val updateProcessMessage = UpdateProcessMessage(id, status)

        assertEquals(id, updateProcessMessage.id)
        assertEquals(status, updateProcessMessage.status)
    }
}