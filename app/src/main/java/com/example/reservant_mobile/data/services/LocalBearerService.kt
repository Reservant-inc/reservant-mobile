package com.example.reservant_mobile.data.services

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.reservant_mobile.ApplicationService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull


class LocalBearerService{

    companion object {
        private val context  = ApplicationService.instance
        private val Context.dataStore by preferencesDataStore(context.packageName)
        private val bearerTokenKey = stringPreferencesKey("bearer_token")
    }

        suspend fun saveBearerToken(bearerToken: String) {
        context.dataStore.edit {
            it[bearerTokenKey] = bearerToken
        }
    }

    suspend fun getBearerToken(): String {
        val test = context.dataStore.data.firstOrNull()?.get(bearerTokenKey).orEmpty()
        println("CURRENT TOKEN; "+test)
            return test
    }

}