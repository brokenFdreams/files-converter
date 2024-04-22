package com.brokenfdreams.filesconverter.model

open class Field(open val name: String, open val type: String) {
    open fun getJavaFieldRow():String {
        return "    private final $type $name;"
    }
}

class ArrayField(override val name: String, override val type: String, val subType: String): Field(name, type) {
    override fun getJavaFieldRow(): String {
        return "    private final $type<$subType> $name;"
    }
}

class ObjectField(override val name: String, override val type: String, val fields: List<Field>): Field(name, type)

class ArrayOfObjectsField(override val name: String, override val type: String, val subType: String, val fields: List<Field>): Field(name, type) {
    override fun getJavaFieldRow(): String {
        return "    private final $type<$subType> $name;"
    }
}
