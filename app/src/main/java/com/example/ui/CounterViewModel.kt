package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CounterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CounterUiState(
    val count: Long = 0L,
    val hapticsEnabled: Boolean = true
)

class CounterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CounterRepository(application)

    val uiState: StateFlow<CounterUiState> = combine(
        repository.count,
        repository.hapticsEnabled
    ) { count, hapticsEnabled ->
        CounterUiState(
            count = count,
            hapticsEnabled = hapticsEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CounterUiState(
            count = repository.count.value,
            hapticsEnabled = repository.hapticsEnabled.value
        )
    )

    fun increment() {
        repository.updateCount(uiState.value.count + 1)
    }

    fun reset() {
        repository.resetCount()
    }

    fun toggleHaptics() {
        repository.toggleHaptics()
    }
}
