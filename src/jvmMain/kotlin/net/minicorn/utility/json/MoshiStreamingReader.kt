package net.minicorn.utility.json

import com.squareup.moshi.JsonReader
import okio.BufferedSource
import okio.Okio
import java.io.File


fun main() {
    Okio.source(File("file.json")).use { fileSource ->
        Okio.buffer(fileSource).use { bufferedSource ->
            MoshiStreamingReader().readJsonStream(bufferedSource)

//            while (true) {
//                val line: String = bufferedSource.readUtf8Line() ?: break
//                if (line.contains("square")) {
//                    println(line)
//                }
//            }
        }
    }


}

data class Message(val id: Int, val text: String, val user: User, val geo: String)
data class User(val id: Int)

/**
 * @see https://medium.com/@BladeCoder/advanced-json-parsing-techniques-using-moshi-and-kotlin-daf56a7b963d
 */
class MoshiStreamingReader {

    fun readJsonStream(source: BufferedSource?) {
        val reader = JsonReader.of(source)
        reader.use { reader -> readRoot(reader) }
    }

    fun readRoot(reader: JsonReader) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "sections") {
                val sections = readSections(reader)
                printSections(sections)
            } else {
                IndentedPrint.printIndent("$name ${reader.readJsonValue()}")
            }
//        }
        }
        reader.endObject()
    }

    private fun readSections(reader: JsonReader): MutableList<Section> {
        val sections = mutableListOf<Section>()
        reader.beginArray()
        while (reader.hasNext()) {
            val section = readSection(reader)
            sections.add(section)
        }
        reader.endArray()
        return sections
    }

    private fun readSection(reader: JsonReader): Section {
        val section = Section()
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "title" -> section.title = reader.nextString()
                "key" -> section.key = reader.nextString()
                "fields" -> {
                    if (reader.peek() != JsonReader.Token.NULL) section.fields = readFields(reader)
                    else reader.nextNull<Unit>()
                }
                "sections" -> {
                    if (reader.peek() != JsonReader.Token.NULL) section.sections = readSections(reader)
                    else reader.nextNull<Unit>()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return section
    }

    private fun readFields(reader: JsonReader): MutableList<Field> {
        val fields = mutableListOf<Field>()
        reader.beginArray()
        while (reader.hasNext()) {
            val field = readField(reader)
            fields.add(field)
        }
        reader.endArray()
        return fields

    }

    private fun readField(reader: JsonReader): Field {
        val field = Field()
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "key" -> field.key = reader.nextString()
                "typeField" -> field.typeField = reader.nextString()
                "validation" -> field.required = readValidation(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return field
    }

    private fun readValidation(reader: JsonReader): Boolean {
        var required = false
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "required" -> required  = reader.nextBoolean()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return required
//        println(reader.readJsonValue().toString())
    }

    private fun printSections(sections: MutableList<Section>) {
        IndentedPrint.addIndent()
        sections.forEach { section ->
            IndentedPrint.printIndent("${section.title} : ${section.key}")
            section.fields?.let {
                IndentedPrint.addIndent()
                printFields(it)
                IndentedPrint.subIndent()
            }
            section.sections?.let {
                printSections(it)
            }
        }
        IndentedPrint.subIndent()
    }

    private fun printFields(fields: MutableList<Field>) {
        fields.forEach{ field ->
            if(!field.required && field.typeField != "hidden")
                IndentedPrint.printIndent("${field.key} : required ${field.required}: typeField ${field.typeField}")
        }
    }

}

class Section {
    var title: String = ""
    var key: String = ""
    var sections: MutableList<Section>? = null
    var fields: MutableList<Field>? = null
}

class Field {
    var key: String = ""
    var typeField: String = ""
    var required: Boolean = false
//    var validation: Validation? = null
}

class Validation {
    var required: Boolean = false
}

object IndentedPrint {
    var numIndents = 0
    var indent = ""

    fun addIndent() {
        numIndents++
        buildIndent()
    }

    fun subIndent() {
        numIndents--
        buildIndent()
    }

    fun resetIndent() {
        numIndents = 0
        indent = ""
    }

    private fun buildIndent() {
        indent = ""
        for (i in 0 until numIndents) {
//            indent += "\t"
            indent += "  "
        }
    }

    fun printIndent(text: String) {
        println("$indent$text")
    }
}