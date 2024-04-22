package com.brokenfdreams.filesconverter.service

import com.brokenfdreams.filesconverter.model.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class XmlFileConverterService {

    private val xmlObjectMapper = XmlMapper().registerModule(JavaTimeModule())


    fun convertToJava(inputStream: InputStream): String {
        val rootNode = xmlObjectMapper.readTree(inputStream)

        val fields = rootNode.fields().asSequence()
            .map { mapXmlFieldToField(it.key, it.value) }
            .toList()

        return generateClassContent("MainClassToRename", fields)
    }

    private fun generateClassContent(className: String, fields: List<Field>): String {
        val fieldsContent = fields
            .map { it.getJavaFieldRow() }
            .reduce { left, right -> "$left\n$right" }

        val dependentClasses = (fields
            .filterIsInstance<ObjectField>()
            .map { Pair(it.type, it.fields) }
            .toSet() +
                fields
                    .filterIsInstance<ArrayOfObjectsField>()
                    .map { Pair(it.subType, it.fields) }
                    .toSet())
            .map { generateClassContent(it.first, it.second) }
            .takeIf { it.isNotEmpty() }
            ?.reduce { left, right -> "$left\n$right" } ?: ""

        return """
class $className {
$fieldsContent
}

$dependentClasses
""".trimIndent()
    }

    private fun mapXmlFieldToField(fieldName: String, fieldNode: JsonNode): Field {
        return when (fieldNode.nodeType) {
            JsonNodeType.STRING -> Field(fieldName, "String")
            JsonNodeType.BOOLEAN -> Field(fieldName, "boolean")
            JsonNodeType.NUMBER ->
                Field(
                    fieldName, when {
                        fieldNode.isLong -> "long"
                        fieldNode.isInt -> "int"
                        fieldNode.isShort -> "short"
                        fieldNode.isDouble -> "double"
                        fieldNode.isBigDecimal -> "BigDecimal"
                        fieldNode.isFloat -> "float"
                        fieldNode.isBigInteger -> "BigInteger"
                        else -> "long"
                    }
                )
            //TODO: Are they objects?
            JsonNodeType.OBJECT -> mapXmlObjectToJava(fieldName, fieldNode)
            JsonNodeType.POJO -> mapXmlObjectToJava(fieldName, fieldNode)

            JsonNodeType.ARRAY -> mapArrayToJavaArray(fieldName, fieldNode)
            //TODO: All of them are strings?
            JsonNodeType.NULL -> Field(fieldName, "String")
            JsonNodeType.MISSING -> Field(fieldName, "String")
            JsonNodeType.BINARY -> Field(fieldName, "String")
            null -> Field(fieldName, "String")
        }
    }

    private fun mapXmlObjectToJava(fieldName: String, fieldNode: JsonNode): Field {
        val fields = fieldNode.fields().asSequence()
            .map { mapXmlFieldToField(it.key, it.value) }
            .toList()
        return ObjectField(fieldName, fieldName.replaceFirstChar { it.uppercase() }, fields)
    }

    private fun mapArrayToJavaArray(fieldName: String, fieldNode: JsonNode): Field {
        if (fieldNode is ArrayNode) {
            val field = fieldNode.fields().asSequence()
                .map { mapXmlFieldToField(it.key, it.value) }
                .first()
            if (field is ObjectField) {
                return ArrayOfObjectsField(fieldName, "List", field.type, listOf(field))
            }
        }
        return ArrayField(fieldName, "List", field.type)
    }
}
