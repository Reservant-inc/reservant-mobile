package com.example.reservant_mobile.data.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.ByteArrayOutputStream

class GetFileFromURIUtil {

    fun getFileDataFromUri(context: Context, uri: Uri): ByteArray? {
        val contentResolver: ContentResolver = context.contentResolver
        val outputStream = ByteArrayOutputStream()

        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val buffer = ByteArray(4 * 1024) // buffer size
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return outputStream.toByteArray()
    }
}
