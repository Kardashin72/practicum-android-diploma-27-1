package ru.practicum.android.diploma.search.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageInteractor
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageRepository
import ru.practicum.android.diploma.search.domain.model.VacancyFilter

class VacancyFilterStorageInteractorImpl(val repository: VacancyFilterStorageRepository) :
    VacancyFilterStorageInteractor {
    override fun getFilters(): Flow<VacancyFilter> {
        return repository.getFilters().map {
            it ?: VacancyFilter()
        }
    }

    override suspend fun saveFilters(filters: VacancyFilter) {
        repository.saveFilters(filters)
    }

    override suspend fun clearFilters() {
        repository.clearFilters()
    }
}
