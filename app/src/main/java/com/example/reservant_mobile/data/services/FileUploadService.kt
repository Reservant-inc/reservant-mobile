package com.example.reservant_mobile.data.services

import com.example.reservant_mobile.ui.constants.Endpoints
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class FileUploadService(private var api: APIService = APIServiceImpl()) {
    companion object{
        const val PDF = "application/pdf"
        const val PNG = "image/png"
        const val JPG = "image/jpeg"
    }

     suspend fun sendFile(contentType: String, f: ByteArray): String {
        val content = MultiPartFormDataContent(
             formData {
             append("file", f, Headers.build {
                 append(HttpHeaders.ContentType, contentType)
                 append(HttpHeaders.ContentDisposition, "filename=\"${contentType}_file\"")
             })
         }
        )

        val res = api.post(content, Endpoints.FILE_UPLOADS) ?: return "Error 1"

        if (res.status.value == 200) return res.body()
        if (res.status.value == 401) return "Error 2: Unauthorized"

        return "Error 3"
    }

}