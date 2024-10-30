package reservant_mobile.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.FileService
import java.util.regex.Pattern

open class ReservantViewModel(
    val fileService: FileService = FileService()
): ViewModel() {

    suspend fun fetchPhoto(photo: String): Bitmap? {
        val result = fileService.getImage(photo)

        return if (!result.isError) {
            result.value
        } else {
            null
        }
    }

    protected fun <T> getFieldError(result: Result<T>, name: String): Int {
        if (!result.isError) {
            return -1
        }

        return result.errors?.getOrDefault(name, -1) ?: -1
    }

    fun<T> getToastError(result: Result<T>): Int {
        return getFieldError(result, "TOAST")
    }

    protected fun isInvalidWithRegex(regex: String, str: String): Boolean {
        return !Pattern.matches(regex, str)
    }
}