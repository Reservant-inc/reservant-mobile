package reservant_mobile.data.services

import android.graphics.Bitmap
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.example.reservant_mobile.R
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import reservant_mobile.ApplicationService
import reservant_mobile.data.endpoints.Uploads
import reservant_mobile.data.models.dtos.FileUploadDTO
import reservant_mobile.data.models.dtos.fields.Result


enum class DataType(val dType: String) {
    PDF("application/pdf"),
    JPG("image/jpeg"),
    PNG("image/png")
}
class FileService(): ServiceUtil() {
    val context = ApplicationService.instance


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

    suspend fun getFile(fileName: String): Result<ByteArray?> {
        val res = api.get(fileName)
        return complexResultWrapper(res)
    }

    suspend fun getImage(imageFileName: String): Result<Bitmap?> {
        val loader = context.imageLoader
        var res: Result<Bitmap?> = Result(
            isError = true,
            value = null
        )
        val request = ImageRequest.Builder(context)
            .data("${api.backendUrl}$imageFileName")
            .allowHardware(false)
            .target(
                onSuccess = { result ->
                    res = Result(
                        isError = false,
                        value = result.toBitmap()
                    )
                },
                onError = { _ ->
                    res = Result(
                        isError = true,
                        value = null
                    )
                }
            )
            .build()
        loader.execute(request)

        return res

    }
}