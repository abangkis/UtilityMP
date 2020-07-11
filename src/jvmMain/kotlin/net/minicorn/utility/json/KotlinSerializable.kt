package net.minicorn.utility.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.minicorn.utility.form.FormModel
import java.io.File

//@Serializable
//data class Data(val a: Int, val b: String = "42")

fun main() {
    val jsonString = File("file.json").readText()

    KotlinSerializable().parse(jsonString)
}

class KotlinSerializable {

    fun parse(jsonString: String) {
        val json = Json(JsonConfiguration.Stable)

        val obj = json.parse(FormModel.serializer(), jsonString)
        println(obj) // Data(a=42, b="42")
    }

}

private fun parseOri() {
    // Json also has .Default configuration which provides more reasonable settings,
    // but is subject to change in future versions
    val json = Json(JsonConfiguration.Stable)
    // serializing objects
//    val jsonData = json.stringify(FormModel.serializer(), Data(42))
//    // serializing lists
//    val jsonList = json.stringify(Data.serializer().list, listOf(Data(42)))
//    println(jsonData) // {"a": 42, "b": "42"}
//    println(jsonList) // [{"a": 42, "b": "42"}]

    // parsing data back
    val obj = json.parse(FormModel.serializer(), """{"a":42}""") // b is optional since it has default value
    println(obj) // Data(a=42, b="42")
}

