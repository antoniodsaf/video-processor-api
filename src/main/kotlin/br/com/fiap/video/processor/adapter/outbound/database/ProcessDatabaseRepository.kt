package br.com.fiap.video.processor.adapter.outbound.database

import br.com.fiap.video.processor.adapter.outbound.database.entity.ProcessEntity
import br.com.fiap.video.processor.adapter.outbound.database.jpa.ProcessJpaRepository
import br.com.fiap.video.processor.application.core.domain.Process
import br.com.fiap.video.processor.application.core.domain.valueobject.ProcessId
import br.com.fiap.video.processor.application.port.outbound.process.ProcessRepository
import br.com.fiap.video.processor.application.port.outbound.process.dto.ProcessOutboundResponse
import kotlin.jvm.optionals.getOrNull

open class ProcessDatabaseRepository(private val repository: ProcessJpaRepository) :
    ProcessRepository {

    override fun save(process: Process): ProcessOutboundResponse {
        return repository.save(ProcessEntity.from(process)).toOutbound()
    }

    override fun findById(processId: ProcessId): ProcessOutboundResponse? {
        return repository.findById(processId.value).map { it.toOutbound() }.getOrNull()
    }

    override fun findByUser(user: String): List<ProcessOutboundResponse> {
        return repository.findByUser(user).map { it.toOutbound() }
    }
}
