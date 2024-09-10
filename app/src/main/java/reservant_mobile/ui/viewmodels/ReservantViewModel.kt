package reservant_mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.FileService

open class ReservantViewModel(
    private val fileService: FileService = FileService()
): ViewModel() {
    
    protected fun <T> getFieldError(result: Result<T>, name: String): Int {
        if (!result.isError) {
            return -1
        }

        return result.errors?.getOrDefault(name, -1) ?: -1
    }
}