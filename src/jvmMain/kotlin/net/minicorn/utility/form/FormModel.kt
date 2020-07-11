package net.minicorn.utility.form

import kotlinx.serialization.Serializable

@Serializable
data class FormModel(
    var formType: String,
    val model: String? = null,
    val sections: List<Section>? = null,
    val version: String? = null
)

@Serializable
data class Section(
    val fields: List<Field>?,
    val sections: List<Section>? = null,
    val key: String,
    val title: String,
    val type: String,
    val description: String?,
    val icon: String? = "",
    var progress: Float = 0f,
    var validated: Boolean = false
)

@Serializable
data class Field(
    val key: String,
    val label: String? = "",
    var options: List<FormOptions>? = null,
    val typeField: String,
    val default: String? = "",
    val validation: Validation? = null,
    val ageKey: String? = "",
    val idKey: String? = "",
    val parentKey: String? = "",
    val childKey: String? = "",
    val dataSource: String? = "",
    val description: String? = "",
    val icon: String? ="",
    var answer: String? = "",
    var fields: List<Field>? = null
)

@Serializable
data class FormOptions(
    val label: String? = "",
    val value: String,
    // fixme ada option yang gak punya key?
    val key: String? = "",
    val parentValue: String? = null,
    val options: List<FormOptions>? = null
)

@Serializable
data class Validation(
    var id: Int = 0,
    val required: Boolean? = false,
    val exactLength: Int? = 0,
    val minLength: Int? = 0,
    val maxLength: Int? = 0,
    val _default: String? = "",
    val conditions: List<Condition>? = null
)

@Serializable
data class Condition(
    val id: Int = 0,
    val value: String? = "",
    val operations: Operation? = null
)

@Serializable
data class Operation(
    val id: Int = 0,
    val operand: String? = "",
    val operator: String? = "",
    val comparator: String? = ""
)