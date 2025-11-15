package com.nextdns.protector.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nextdns_settings")

class PreferencesManager(private val context: Context) {

    companion object {
        private val DNS_ENABLED = booleanPreferencesKey("dns_enabled")
        private val DEVICE_ADMIN_ENABLED = booleanPreferencesKey("device_admin_enabled")
        private val DNS_METHOD = stringPreferencesKey("dns_method")
        private val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
        private val LOGS_ENABLED = booleanPreferencesKey("logs_enabled")
        private val PARENTAL_CONTROL_ENABLED = booleanPreferencesKey("parental_control_enabled")
    }

    val dnsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DNS_ENABLED] ?: false
    }

    val deviceAdminEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DEVICE_ADMIN_ENABLED] ?: false
    }

    val dnsMethodFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DNS_METHOD] ?: "auto"
    }

    val analyticsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ANALYTICS_ENABLED] ?: true
    }

    val logsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LOGS_ENABLED] ?: true
    }

    val parentalControlEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PARENTAL_CONTROL_ENABLED] ?: false
    }

    suspend fun setDnsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DNS_ENABLED] = enabled
        }
    }

    suspend fun setDeviceAdminEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_ADMIN_ENABLED] = enabled
        }
    }

    suspend fun setDnsMethod(method: String) {
        context.dataStore.edit { preferences ->
            preferences[DNS_METHOD] = method
        }
    }

    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ANALYTICS_ENABLED] = enabled
        }
    }

    suspend fun setLogsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOGS_ENABLED] = enabled
        }
    }

    suspend fun setParentalControlEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PARENTAL_CONTROL_ENABLED] = enabled
        }
    }

    suspend fun isDnsEnabled(): Boolean {
        return context.dataStore.data.first()[DNS_ENABLED] ?: false
    }
}
