package com.example.reservant_mobile.data.services

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


class LocalBearerService{
    private val context  = ApplicationService.instance
    private val Context.dataStore by preferencesDataStore(context.packageName)

    suspend fun saveBearerToken(bearerToken: String) {
        context.dataStore.edit {
            it[bearerTokenKey] = bearerToken
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        context.dataStore.edit {
            it[refreshTokenKey] = refreshToken
        }
    }

    suspend fun getBearerToken(): String {
        return try {
            context.dataStore.data.first()[bearerTokenKey].toString()
        } catch (_: NoSuchElementException){
            ""
        }
    }

    suspend fun getRefreshToken(): String {
        return try {
            context.dataStore.data.first()[refreshTokenKey].toString()
        } catch (_: NoSuchElementException){
            ""
        }    }

    private val bearerTokenKey = stringPreferencesKey("bearer_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
}