package reservant_mobile.data.utils

import android.os.Bundle
import androidx.navigation.NavType
import com.eygraber.uri.UriCodec
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

fun <T> toCustomNavType(serializer: KSerializer<T>): NavType<T> {
    return object : NavType<T>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): T? {
            val json = bundle.getString(key) ?: return null
            return Json.decodeFromString(serializer, json)
        }

        override fun parseValue(value: String): T {
            val decodedValue = UriCodec.decode(value)
            return Json.decodeFromString(serializer, decodedValue)
        }

        override fun put(bundle: Bundle, key: String, value: T) {
            val json = Json.encodeToString(serializer, value)
            bundle.putString(key, json)
        }

        override fun serializeAsValue(value: T): String {
            val json = Json.encodeToString(serializer, value)
            return UriCodec.encode(json)
        }
    }
}
