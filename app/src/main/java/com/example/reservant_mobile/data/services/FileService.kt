package com.example.reservant_mobile.data.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.endpoints.Uploads
import com.example.reservant_mobile.data.models.dtos.FileUploadDTO
import com.example.reservant_mobile.data.models.dtos.fields.Result
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode


enum class DataType(val dType: String) {
    PDF("application/pdf"),
    JPG("image/jpeg"),
    PNG("image/png")
}
class FileService(private var api: APIService = APIService()) {

     suspend fun sendFile(contentType: DataType, f: ByteArray): Result<FileUploadDTO?> {
        val content = MultiPartFormDataContent(
             formData {
             append("file", f, Headers.build {
                 append(HttpHeaders.ContentType, contentType.dType)
                 append(HttpHeaders.ContentDisposition, "filename=\"${contentType}_file\"")
             })
         }
        )

        val res = api.post(Uploads(), content)
        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

         return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

    suspend fun getFile(fileName: String): Result<ByteArray?>{
        val res = api.get(Uploads.FileName(fileName=fileName))

        if(res.isError)
            return Result(isError = true, errors = res.errors, value = null)

        if (res.value!!.status == HttpStatusCode.OK){
            return try {
                Result(isError = false, value = res.value.body())
            }
            catch (e: Exception){
                Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
            }
        }

        return Result(isError = true, errors = mapOf(Pair("TOAST", R.string.error_unknown)), value = null)
    }

    suspend fun getImage(imageFileName: String): Result<Bitmap?>{
        val res = getFile(imageFileName)

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