package com.brokenfdreams.filesconverter.service

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class CsvFileConverterService {
    fun convertToJava(inputStream: InputStream): String {
        return csvReader().open(inputStream) {
            val headers = readAllWithHeaderAsSequence()
                .map { it.keys }
                .first()
            generateJavaClassFromHeaders(headers)
        }
    }

    private fun generateJavaClassFromHeaders(headers: Set<String>): String {
        val fields = headers.stream()
            .map { "    private final String $it;\n" }
            .reduce { acc, it -> acc + it }

        return """
            public class GeneratedFromCSV {
                $fields
            }
        """.trimIndent()
    }
}
