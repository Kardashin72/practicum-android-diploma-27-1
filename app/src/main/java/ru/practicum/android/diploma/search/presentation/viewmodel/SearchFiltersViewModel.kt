package ru.practicum.android.diploma.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import kotlinx.coroutines.flow.stateIn
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageInteractor
import ru.practicum.android.diploma.search.domain.model.VacancyFilter

class SearchFiltersViewModel(private val interactor: VacancyFilterStorageInteractor) : ViewModel() {

    val filters: StateFlow<VacancyFilter> = interactor.getFilters().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(SUBSCRIBE_TIMEOUT),
        VacancyFilter()
    )

    fun onSalaryChanged(salaryText: String) {
        val salary = salaryText.toIntOrNull()
        _filters.update { it.copy(salary = salary) }
    }

    fun onOnlyWithSalaryChanged(checked: Boolean) {
        _filters.update { it.copy(onlyWithSalary = checked) }
    }

    fun resetFilters() {
        _filters.value = VacancyFilter()
    companion object {
        private const val SUBSCRIBE_TIMEOUT = 5_000L
    }
}
