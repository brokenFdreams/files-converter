package com.brokenfdreams.filesconverter.controller

import com.brokenfdreams.filesconverter.service.CsvFileConverterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("csv/")
class CsvFileConverterController(private val csvFileConverterService: CsvFileConverterService) {


    @PostMapping("to/java")
    fun convertToJava(file: MultipartFile): ResponseEntity<String> {
        return ResponseEntity.ok(csvFileConverterService.convertToJava(file.inputStream))
    }

}