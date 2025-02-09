package br.com.fiap.video.processor.infrastructure.instantiation

import br.com.fiap.video.processor.adapter.outbound.aws.s3.client.S3FileTransfer
import br.com.fiap.video.processor.adapter.outbound.database.ProcessDatabaseRepository
import br.com.fiap.video.processor.adapter.outbound.database.jpa.ProcessJpaRepository
import br.com.fiap.video.processor.application.core.usecase.process.*
import br.com.fiap.video.processor.application.mapper.process.DefaultProcessDomainMapper
import br.com.fiap.video.processor.application.mapper.process.ProcessMapper
import br.com.fiap.video.processor.application.port.inbound.process.*
import br.com.fiap.video.processor.application.port.outbound.process.*
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories("br.com.fiap.video.processor.adapter.outbound.database.jpa")
@EntityScan("br.com.fiap.video.processor.adapter.outbound.database.entity")
class ProcessPortsInstantiationConfig {

    @Bean
    fun processDomainMapper(): ProcessMapper {
        return DefaultProcessDomainMapper()
    }

    @Bean
    fun processRepository(repository: ProcessJpaRepository): ProcessRepository {
        return (@Transactional object : ProcessDatabaseRepository(repository) {})
    }

    @Bean
    fun createProcessService(
        processMapper: ProcessMapper,
        processRepository: ProcessRepository,
        processSentMessenger: ProcessSentMessenger,
        uploader: FileUploader
    ): CreateProcessService {
        return CreateProcessUseCase(
            processMapper,
            processRepository,
            processSentMessenger,
            uploader
        )
    }

    @Bean
    fun updateProcessStatusService(
        processMapper: ProcessMapper,
        processRepository: ProcessRepository,
    ): UpdateProcessStatusService {
        return UpdateProcessStatusUseCase(
            processMapper,
            processRepository,
        )
    }

    @Bean
    fun downloadFramesZipService(
        fileDownloader: FileDownloader,
        processRepository: ProcessRepository,
    ): DownloadFramesZipService {
        return DownloadFramesZipUseCase(
            fileDownloader,
            processRepository,
        )
    }

    @Bean
    fun getProcessByUserService(
        processMapper: ProcessMapper,
        processRepository: ProcessRepository,
    ): GetProcessByUserService {
        return GetProcessByUserUseCase(processMapper, processRepository)
    }

}
