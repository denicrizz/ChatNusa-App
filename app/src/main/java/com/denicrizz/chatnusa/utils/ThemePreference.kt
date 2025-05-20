package com.denicrizz.chatnusa.utils
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object ThemePreference {
    private val THEME_KEY = booleanPreferencesKey("dark_theme")

    fun getTheme(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false // default: terang
        }
    }

    suspend fun setTheme(context: Context, isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkTheme
        }
    }
}