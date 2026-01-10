package ru.practicum.android.diploma.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.search.domain.model.VacancyFilter

interface VacancyFilterStorageRepository {
    fun getFilters(): Flow<VacancyFilter?>
    suspend fun saveFilters(filters: VacancyFilter)
    suspend fun clearFilters()
}
