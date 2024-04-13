package com.example.reservant_mobile.data.models.dtos.fields

import org.json.JSONObject

class Result<T>(
    val isError: Boolean,
    val value: T
){
    constructor(
        isError: Boolean,
        errors: JSONObject? = null,
        value: T
    ): this(
        isError=isError,
        value=value
    ){
        this.errors = errors?.keys()?.asSequence()?.associateWith {
            Integer.parseInt(errors.getJSONArray(it)[0].toString())
        }
    }

    constructor(
        isError: Boolean,
        errors: Map<String, Int>? = null,
        value: T
    ) : this(
        isError=isError,
        value=value
    ){
        this.errors = errors
    }

    var errors: Map<String, Int>? = null
        get() = if (!isError) throw UnsupportedOperationException() else field

}