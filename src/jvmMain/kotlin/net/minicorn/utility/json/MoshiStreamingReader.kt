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

    private fun printSections(sections: MutableList<Section>) {
        sections.forEach { section ->
            IndentedPrint.addIndent()
            IndentedPrint.printIndent("${section.title} : ${section.key}")
            IndentedPrint.printIndent(section.fields)
            section.sections?.let { printSections(it) }
            IndentedPrint.subIndent()
        }
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
                    if (reader.peek() != JsonReader.Token.NULL) readFields(reader)
                    else reader.nextNull<Unit>()
                }
                "sections" -> {
                    if (reader.peek() != JsonReader.Token.NULL) readSections(reader)
                    else reader.nextNull<Unit>()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return section
    }

    private fun readFields(reader: JsonReader) {
        println(reader.readJsonValue().toString())
    }

//    @Throws(IOException::class)
//    fun readMessagesArray(reader: JsonReader): List<Message?>? {
//        val messages: MutableList<Message?> = ArrayList<Message?>()
//        reader.beginArray()
//        while (reader.hasNext()) {
//            messages.add(readMessage(reader))
//        }
//        reader.endArray()
//        return messages
//    }
//
//    @Throws(IOException::class)
//    fun readMessage(reader: JsonReader): Message? {
//        var id: Long = -1
//        var text: String? = null
//        var user: User? = null
//        var geo: List<Double?>? = null
//        reader.beginObject()
//        while (reader.hasNext()) {
//            val name = reader.nextName()
//            if (name == "id") {
//                id = reader.nextLong()
//            } else if (name == "text") {
//                text = reader.nextString()
//            } else if (name == "geo" && reader.peek() != JsonReader.Token.NULL) {
//                geo = readDoublesArray(reader)
//            } else if (name == "user") {
//                user = readUser(reader)
//            } else {
//                reader.skipValue()
//            }
//        }
//        reader.endObject()
////    return Message(id, text, user, geo)
//        return null
//    }


}

class Section {
    var title: String = ""
    var key: String = ""
    var sections: MutableList<Section>? = null
    var fields: String = ""
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
        for (i in 0 until numIndents) {
//            indent += "\t"
            indent += "  "
        }
    }

    fun printIndent(text: String) {
        println("$indent$text")
    }
}