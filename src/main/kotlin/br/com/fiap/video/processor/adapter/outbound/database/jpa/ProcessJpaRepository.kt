package br.com.fiap.video.processor.adapter.outbound.database.jpa

import br.com.fiap.video.processor.adapter.outbound.database.entity.ProcessEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProcessJpaRepository : JpaRepository<ProcessEntity, String> {
    fun findByUser(user: String): List<ProcessEntity>
}