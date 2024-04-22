package com.brokenfdreams.filesconverter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FilesConverter

fun main(args: Array<String>) {
	runApplication<FilesConverter>(*args)
}
