package ru.practicum.android.diploma.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageInteractor
import ru.practicum.android.diploma.search.domain.model.VacancyFilter

class SearchFiltersViewModel(private val interactor: VacancyFilterStorageInteractor) : ViewModel() {


    val filters: StateFlow<VacancyFilter> = interactor.getFilters().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000), VacancyFilter()
    )

}
