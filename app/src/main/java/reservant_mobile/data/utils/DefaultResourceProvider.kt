package reservant_mobile.data.utils

import android.content.Context
import androidx.annotation.StringRes

class DefaultResourceProvider(private val context: Context) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}