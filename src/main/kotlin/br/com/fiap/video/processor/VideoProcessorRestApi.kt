package br.com.fiap.video.processor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["br.com.fiap.video.processor", "br.com.fiap.user"])
class VideoProcessorRestApi

fun main(args: Array<String>) {
	runApplication<VideoProcessorRestApi>(*args)
}
