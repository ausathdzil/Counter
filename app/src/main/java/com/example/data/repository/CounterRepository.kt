package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.edit

class CounterRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("counter_prefs", Context.MODE_PRIVATE)

    private val _count = MutableStateFlow(prefs.getLong(KEY_COUNT, 0L))
    val count: StateFlow<Long> = _count.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTICS, true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    fun updateCount(newCount: Long) {
        val safeCount = maxOf(0L, newCount)
        _count.value = safeCount
        prefs.edit { putLong(KEY_COUNT, safeCount) }
    }

    fun resetCount() {
        _count.value = 0L
        prefs.edit { putLong(KEY_COUNT, 0L) }
    }

    fun toggleHaptics() {
        val updated = !_hapticsEnabled.value
        _hapticsEnabled.value = updated
        prefs.edit { putBoolean(KEY_HAPTICS, updated) }
    }

    companion object {
        private const val KEY_COUNT = "key_count"
        private const val KEY_HAPTICS = "key_haptics"
    }
}
