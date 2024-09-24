package reservant_mobile.data.services

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import reservant_mobile.ApplicationService
import reservant_mobile.data.constants.PrefsKeys


class LocalDataService{

    companion object {
        private val context  = ApplicationService.instance
        private val Context.dataStore by preferencesDataStore(context.packageName)
    }

    suspend fun saveData(key: PrefsKeys, data: String) {
        context.dataStore.edit {
            it[stringPreferencesKey(key.keyName)] = data
        }
    }

    suspend fun getData(key: PrefsKeys): String {
            return context.dataStore.data.firstOrNull()?.get(stringPreferencesKey(key.keyName)).orEmpty()
    }

}