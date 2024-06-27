package reservant_mobile.data.models.dtos.fields

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FormField(
    val name: String
){
    var value by mutableStateOf("")
    fun <T> getError(result: Result<T>): Int{
        if (!result.isError) {
            return -1
        }

        return result.errors?.getOrDefault(name, -1) ?: -1
    }
}