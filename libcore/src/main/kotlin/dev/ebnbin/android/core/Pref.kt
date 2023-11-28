package dev.ebnbin.android.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

open class Pref<V, SV : Any>(
    val name: String,
    val encrypted: Boolean = false,
    val key: String,
    val defaultValue: () -> V,
    val valueToStoredValue: (V) -> SV,
    val storedValueToValue: (SV) -> V,
): LiveData<V>(defaultValue()), SharedPreferences.OnSharedPreferenceChangeListener {
    private fun sharedPreferences(): SharedPreferences {
        return sharedPreferences(name, encrypted)
    }

    //*****************************************************************************************************************

    fun defaultStoredValue(): SV {
        return valueToStoredValue(defaultValue())
    }

    fun getStoredValue(): SV {
        return sharedPreferences().get(key, defaultStoredValue())
    }

    fun commitStoredValue(storedValue: SV): Boolean {
        return putStoredValue(storedValue, commit = true)
    }

    fun commitStoredValue(updater: (SV) -> SV): Boolean {
        val storedValue = getStoredValue()
        return commitStoredValue(updater(storedValue))
    }

    fun applyStoredValue(storedValue: SV) {
        putStoredValue(storedValue, commit = false)
    }

    fun applyStoredValue(updater: (SV) -> SV) {
        val storedValue = getStoredValue()
        applyStoredValue(updater(storedValue))
    }

    protected fun putStoredValue(storedValue: SV, commit: Boolean): Boolean {
        if (getStoredValue() == storedValue) {
            return true
        }
        return sharedPreferences().edit(commit) {
            put(key, storedValue)
        }
    }

    //*****************************************************************************************************************

    private fun updateAndGet(): V {
        val value = storedValueToValue(getStoredValue())
        if (super.getValue() != value) {
            super.setValue(value)
        }
        return value
    }

    fun commit(value: V): Boolean {
        return put(value, commit = true)
    }

    fun commit(updater: (V) -> V): Boolean {
        val value = updateAndGet()
        return commit(updater(value))
    }

    fun apply(value: V) {
        put(value, commit = false)
    }

    fun apply(updater: (V) -> V) {
        val value = updateAndGet()
        apply(updater(value))
    }

    private fun put(value: V, commit: Boolean): Boolean {
        val storedValue = valueToStoredValue(value)
        return putStoredValue(storedValue, commit)
    }

    //*****************************************************************************************************************

    fun mapStoredValue(): LiveData<SV> {
        return map {
            valueToStoredValue(it)
        }
    }

    override fun onActive() {
        super.onActive()
        updateAndGet()
        sharedPreferences().registerOnSharedPreferenceChangeListener(this)
    }

    override fun onInactive() {
        sharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
        super.onInactive()
    }

    override fun getValue(): V {
        return updateAndGet()
    }

    @Deprecated("", ReplaceWith(""))
    final override fun setValue(value: V) {
        error(Unit)
    }

    @Deprecated("", ReplaceWith(""))
    final override fun postValue(value: V) {
        error(Unit)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences() !== sharedPreferences || this.key != key) {
            return
        }
        updateAndGet()
    }

    companion object {
        private val sharedPreferencesMap: MutableMap<Pair<String?, Boolean>, SharedPreferences> = mutableMapOf()

        private fun sharedPreferences(name: String, encrypted: Boolean): SharedPreferences {
            return sharedPreferencesMap.getOrPut(name to encrypted) {
                if (encrypted) {
                    EncryptedSharedPreferences.create(
                        name,
                        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                        coreApp,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
                    )
                } else {
                    coreApp.getSharedPreferences(name, Context.MODE_PRIVATE)
                }
            }
        }

        private fun <T : Any> SharedPreferences.get(key: String, defaultValue: T): T {
            @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
            return when (defaultValue) {
                is String -> getString(key, defaultValue)
                is Set<*> -> getStringSet(key, defaultValue as Set<String>)
                is Int -> getInt(key, defaultValue)
                is Long -> getLong(key, defaultValue)
                is Float -> getFloat(key, defaultValue)
                is Boolean -> getBoolean(key, defaultValue)
                is Unit -> Unit
                else -> error(Unit)
            } as T
        }

        private fun <T : Any> SharedPreferences.Editor.put(key: String, value: T): SharedPreferences.Editor {
            @Suppress("UNCHECKED_CAST")
            return when (value) {
                is String -> putString(key, value)
                is Set<*> -> putStringSet(key, value as Set<String>)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
                is Unit -> this
                else -> error(Unit)
            }
        }

        @SuppressLint("ApplySharedPref")
        private inline fun SharedPreferences.edit(
            commit: Boolean = false,
            action: SharedPreferences.Editor.() -> Unit,
        ): Boolean {
            val editor = edit()
            action(editor)
            return if (commit) {
                editor.commit()
            } else {
                editor.apply()
                true
            }
        }
    }
}
