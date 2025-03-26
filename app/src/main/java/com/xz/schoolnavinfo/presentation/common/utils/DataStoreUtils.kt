package com.xz.schoolnavinfo.presentation.common.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 创建 DataStore 实例
private val Context.dataStore by preferencesDataStore(name = "app_prefs")

object DataStoreUtils {

    /**
     * 存储数据
     */
    suspend fun <T> saveData(context: Context, key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    /**
     * 读取数据（带默认值）
     */
    suspend fun <T> getData(context: Context, key: Preferences.Key<T>, defaultValue: T): T {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }.first()
    }

    /**
     * 删除某个键的数据
     */
    suspend fun <T> removeData(context: Context, key: Preferences.Key<T>) {
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    /**
     * 清空所有数据
     */
    suspend fun clearAll(context: Context) {
        context.dataStore.edit { it.clear() }
    }

    /**
     * 创建 Key
     */
    object Keys {
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
    }
}
