package ru.practicum.android.diploma.search.data.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageRepository
import ru.practicum.android.diploma.search.domain.model.VacancyFilter

class VacancyFilterStorageRepositoryImpl(
    private val storage: PrefsStorageClient<VacancyFilter>
) : VacancyFilterStorageRepository {
    override fun getFilters(): Flow<VacancyFilter?> {
        return flowOf(storage.getData())
    }

    override suspend fun saveFilters(filters: VacancyFilter) {
        storage.storeData(filters)
    }

    override suspend fun clearFilters(filters: VacancyFilter) {
        storage.clearData()
    }
}
