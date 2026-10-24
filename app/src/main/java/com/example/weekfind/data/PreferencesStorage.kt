package com.example.weekfind.data

import android.content.SharedPreferences

interface PreferencesStorage {
    fun getString(key: String, defaultValue: String?): String?
    fun putString(key: String, value: String?)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun remove(key: String)
}

class SharedPreferencesStorage(
    private val sharedPreferences: SharedPreferences
) : PreferencesStorage {

    override fun getString(key: String, defaultValue: String?): String? =
        sharedPreferences.getString(key, defaultValue)

    override fun putString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}

class InMemoryPreferencesStorage : PreferencesStorage {
    private val strings = mutableMapOf<String, String?>()
    private val booleans = mutableMapOf<String, Boolean>()

    override fun getString(key: String, defaultValue: String?): String? =
        if (strings.containsKey(key)) strings[key] else defaultValue

    override fun putString(key: String, value: String?) {
        if (value == null) {
            strings.remove(key)
        } else {
            strings[key] = value
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        booleans[key] ?: defaultValue

    override fun putBoolean(key: String, value: Boolean) {
        booleans[key] = value
    }

    override fun remove(key: String) {
        strings.remove(key)
        booleans.remove(key)
    }
}
