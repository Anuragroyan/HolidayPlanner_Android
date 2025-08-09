package com.example.holidayplanner.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holidayplanner.model.Holiday
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HolidayViewModel(
    private val repo: HolidayRepository = HolidayRepository()
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        observeHolidays()
    }

    private fun observeHolidays() {
        viewModelScope.launch {
            repo.streamHolidaysWithQuery(_query.value).collectLatest { list ->
                _holidays.value = list
            }
        }
    }

    fun setQuery(q: String) {
        _query.value = q
        viewModelScope.launch {
            repo.streamHolidaysWithQuery(q).collectLatest { _holidays.value = it }
        }
    }

    fun addHoliday(title: String, location: String, notes: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            _loading.value = true
            val h = Holiday(title = title, location = location, notes = notes, startDate = startDate, endDate = endDate)
            val res = repo.createHoliday(h)
            if (res.isFailure) _error.value = res.exceptionOrNull()?.localizedMessage
            _loading.value = false
        }
    }

    fun updateHoliday(holiday: Holiday) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val res = repo.updateHoliday(holiday)
            if (res.isFailure) {
                _error.value = res.exceptionOrNull()?.localizedMessage
            }

            _loading.value = false
        }
    }



    fun deleteHoliday(holiday: Holiday) {
        viewModelScope.launch {
            _loading.value = true
            val res = repo.deleteHoliday(holiday.id) // Pass only the ID string to repo
            if (res.isFailure) {
                _error.value = res.exceptionOrNull()?.localizedMessage
            }
            _loading.value = false
        }
    }

    fun clearError() { _error.value = null }

}