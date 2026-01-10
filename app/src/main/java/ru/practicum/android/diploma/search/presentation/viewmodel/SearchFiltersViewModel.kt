package ru.practicum.android.diploma.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageInteractor
import ru.practicum.android.diploma.search.domain.model.FilterIndustry
import ru.practicum.android.diploma.search.domain.model.VacancyFilter

class SearchFiltersViewModel(private val interactor: VacancyFilterStorageInteractor) : ViewModel() {

    val filters: StateFlow<VacancyFilter> = interactor.getFilters().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(SUBSCRIBE_TIMEOUT),
        VacancyFilter()
    )

    fun onIndustrySelected(industry: FilterIndustry) {
        viewModelScope.launch {
            val current = filters.value
            interactor.saveFilters(
                current.copy(
                    industry = industry.id,
                    industryName = industry.name
                )
            )
        }
    }

    fun clearIndustry() {
        viewModelScope.launch {
            val current = filters.value
            interactor.saveFilters(
                current.copy(
                    industry = null,
                    industryName = null
                )
            )
        }
    }

    fun onSalaryChanged(salaryText: String) {
        val salary = salaryText.toIntOrNull()
        viewModelScope.launch {
            val current = filters.value
            interactor.saveFilters(current.copy(salary = salary))
        }
    }

    fun onOnlyWithSalaryChanged(checked: Boolean) {
        viewModelScope.launch {
            val current = filters.value
            interactor.saveFilters(current.copy(onlyWithSalary = checked))
        }
    }

    fun resetFilters() {
        viewModelScope.launch {
            interactor.clearFilters()
        }
    }

    companion object {
        private const val SUBSCRIBE_TIMEOUT = 5_000L
    }
}
