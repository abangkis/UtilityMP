package net.minicorn.utility.json

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.minicorn.utility.form.FormModel
import java.io.File

fun main() {
    val jsonString = File("file.json").readText()

    MoshiReader().parse(jsonString)
}

class MoshiReader {
    fun parse(json: String) {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(FormModel::class.java)

        val model = jsonAdapter.fromJson(json)
        println(model)
    }
}