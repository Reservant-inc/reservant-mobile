package reservant_mobile.data.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

class DefaultResourceProvider(private val context: Context) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }
    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}