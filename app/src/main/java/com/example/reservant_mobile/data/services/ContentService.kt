package com.example.reservant_mobile.data.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.reservant_mobile.data.models.dtos.fields.Result


class ContentService(private val fileService: FileService = FileService()) {

    suspend fun getImage(imagePath: String): Result<Bitmap?>{
        val res = fileService.getFile(imagePath)

        return when{
            !res.isError -> Result(
                isError = false,
                value = BitmapFactory.decodeByteArray(res.value, 0, res.value!!.size)
            )
            else -> Result(
                isError = true,
                value = null,
                errors = res.errors
            )
        }

    }

}