package reservant_mobile.data.utils

import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun showToast(message: String)
}
