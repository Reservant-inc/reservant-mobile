package com.example.reservant_mobile.data.utils

val reg = Regex("\\{.*\\}")

fun String.insertParams(vararg params: Any) : String =
    params.fold(this) { acc, p ->
        acc.replaceFirst(reg, p.toString())
    }