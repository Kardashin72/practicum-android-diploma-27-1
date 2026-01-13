package ru.practicum.android.diploma.search.data.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageRepository
import ru.practicum.android.diploma.search.domain.model.VacancyFilter

class VacancyFilterStorageRepositoryImpl(
    private val storage: StorageClient<VacancyFilter>
) : VacancyFilterStorageRepository {

    private val filtersState = MutableStateFlow<VacancyFilter?>(storage.getData())

    override fun getFilters(): Flow<VacancyFilter?> {
        return filtersState
    }

    override suspend fun saveFilters(filters: VacancyFilter) {
        storage.storeData(filters)
        filtersState.value = filters
    }

    override suspend fun clearFilters() {
        storage.clearData()
        filtersState.value = null
    }
}
