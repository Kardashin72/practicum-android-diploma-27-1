package ru.practicum.android.diploma.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.model.VacancyFilter

class SearchFiltersViewModel(private val interactor: SearchInteractor) : ViewModel() {

    private val _filters = MutableStateFlow(VacancyFilter())
    val filters: StateFlow<VacancyFilter> = _filters

    fun onSalaryChanged(salaryText: String) {
        val salary = salaryText.toIntOrNull()
        _filters.update { it.copy(salary = salary) }
    }

    fun onOnlyWithSalaryChanged(checked: Boolean) {
        _filters.update { it.copy(onlyWithSalary = checked) }
    }

    fun resetFilters() {
        _filters.value = VacancyFilter()
    }
}
