package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.FileUploadDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import com.example.reservant_mobile.data.models.dtos.fields.Result


enum class DataType(val dType: String) {
    PDF("application/pdf"),
    JPG("image/jpeg"),
    PNG("image/png")
}
class FileUploadService(private var api: APIService = APIServiceImpl()) {

     suspend fun sendFile(contentType: DataType, f: ByteArray): Result<FileUploadDTO?> {
        val content = MultiPartFormDataContent(
             formData {
             append("file", f, Headers.build {
                 append(HttpHeaders.ContentType, contentType.dType)
                 append(HttpHeaders.ContentDisposition, "filename=\"${contentType}_file\"")
             })
         }
        )

        val res = api.post(content, Endpoints.FILE_UPLOADS) ?:
            return Result(true, mapOf(pair = Pair("TOAST", R.string.error_connection_server)), null)


        if (res.status.value == 200){
            if (res.status.value == 200){
                return try {
                    val r:FileUploadDTO = res.body()
                    Result(isError = false, value = r)
                }
                catch (e: Exception){
                    Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unknown)) ,value = null)
                }
            }
        }
         if (res.status.value == 401)
             return Result(isError = true, errors = mapOf(pair= Pair("TOAST", R.string.error_unauthorized_access)) ,value = null)


         return Result(true, mapOf(pair = Pair("TOAST", R.string.error_unknown)), null)
    }

}