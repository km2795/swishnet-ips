package com.sr79.swishnet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sr79.swishnet.model.Stats
import com.sr79.swishnet.model.UiState
import com.sr79.swishnet.repository.StatsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatsViewModel : ViewModel() {

    private val repository = StatsRepository()

    private val _uiState = MutableStateFlow<UiState<Stats>>(UiState.Loading)
    val uiState: StateFlow<UiState<Stats>> = _uiState

    private val _isIpsRunning = MutableStateFlow(false)
    val isIpsRunning: StateFlow<Boolean> = _isIpsRunning

    private var pollingJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val newStats = repository.getStats()
                val currentThreats = (_uiState.value as? UiState.Success)?.data?.threatsBlocked ?: 0L
                _uiState.value = UiState.Success(newStats.copy(threatsBlocked = currentThreats))
            } catch (e: Exception) {
                if (_uiState.value !is UiState.Success) {
                    _uiState.value = UiState.Error("Failed to load stats: ${e.message}")
                }
            }
        }
    }

    fun setIpsRunning(isRunning: Boolean) {
        _isIpsRunning.value = isRunning
    }

    fun updateThreatsBlocked(threatsBlocked: Long) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val newStats = currentState.data.copy(threatsBlocked = threatsBlocked)
            _uiState.value = UiState.Success(newStats)
        }
    }

    fun startPolling() {
        stopPolling() // Ensure only one polling job is running
        pollingJob = viewModelScope.launch {
            while (true) {
                refresh()
                delay(5000) // Poll every 5 seconds
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }
}
