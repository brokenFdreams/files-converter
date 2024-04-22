package com.brokenfdreams.filesconverter.controller

import com.brokenfdreams.filesconverter.service.XmlFileConverterService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("xml/")
class XmlFileConverterController(private val xmlFileConverterService: XmlFileConverterService) {

    @PostMapping("to/java", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun convertToJava(@RequestPart file: MultipartFile): ResponseEntity<String> {
        return ResponseEntity.ok(xmlFileConverterService.convertToJava(file.inputStream))
    }
}