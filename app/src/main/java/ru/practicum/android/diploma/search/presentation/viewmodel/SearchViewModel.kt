package ru.practicum.android.diploma.search.presentation.viewmodel

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.core.presentation.ui.model.VacancyListItemUi
import ru.practicum.android.diploma.core.presentation.ui.util.debounce
import ru.practicum.android.diploma.core.presentation.ui.util.formatSalary
import ru.practicum.android.diploma.favorites.vacancies.domain.api.FavoritesVacanciesInteractor
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageInteractor
import ru.practicum.android.diploma.search.domain.model.Result
import ru.practicum.android.diploma.search.domain.model.VacancyDetail
import ru.practicum.android.diploma.search.domain.model.VacancyFilter
import ru.practicum.android.diploma.search.domain.model.VacancyResponse

class SearchViewModel(
    private val interactor: SearchInteractor,
    private val favoritesInteractor: FavoritesVacanciesInteractor,
    private val vacancyFilterStorageInteractor: VacancyFilterStorageInteractor,
    private val resources: Resources,
) : ViewModel() {
    private val _vacancies = MutableStateFlow<List<VacancyDetail>>(emptyList())
    val vacancies: StateFlow<List<VacancyDetail>> = _vacancies
    var currentPage = 0
    var maxPages = 0

    private val _foundVacancies = MutableStateFlow(0)
    val foundVacancies = _foundVacancies.asStateFlow()

    private var latestSearchText: String? = null
    val vacanciesList = mutableListOf<VacancyListItemUi>()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Nothing)
    val searchState = _searchState.asStateFlow()

    private val _paginationErrorMessage = MutableStateFlow<String?>(null)
    val paginationErrorMessage: StateFlow<String?> = _paginationErrorMessage.asStateFlow()

    private val _textFieldState = MutableStateFlow(SearchTextFieldState())
    val textFieldState = _textFieldState.asStateFlow()

    val searchVacanciesDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) {
        searchVacancies()
    }

    private var onLoadNextPageJob: Job? = null
    private var searchJob: Job? = null

    val hasActiveFilters: StateFlow<Boolean> = vacancyFilterStorageInteractor
        .getFilters()
        .map { filters ->
            filters.salary != null ||
                filters.onlyWithSalary == true ||
                filters.industry != null
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIBE_TIMEOUT),
            initialValue = false
        )

    init {
        observeFavorites()
    }

    fun onQueryChange(query: String) {
        _textFieldState.update {
            it.copy(
                query = query,
                isShowClearIc = query.isNotEmpty()
            )
        }
        searchDebounce(query)

    }

    fun searchVacancies() {
        val newSearchText = textFieldState.value.query
        if (newSearchText.isEmpty()) {
            return
        }
        searchJob?.cancel()
        renderSearchState(SearchState.Loading)
        searchJob = viewModelScope.launch {
            val filter = buildVacancyFilter(page = currentPage)
            interactor.getVacancies(filter).collect { result ->
                when (result) {
                    is Result.Error -> {
                        if (currentPage == 0 && vacanciesList.isEmpty()) {
                            // Ошибка при первой загрузке — показываем экранный плейсхолдер
                            renderSearchState(SearchState.Error(result.message))
                        } else {
                            // Ошибка при догрузке уже существующих данных — тост через paginationErrorMessage
                            renderSearchState(SearchState.Content(vacanciesList, false))
                            _paginationErrorMessage.value = result.message
                        }
                    }

                    is Result.Success<VacancyResponse> -> {
                        processResult(result.data)
                        renderSearchState(SearchState.Content(vacanciesList, false))
                    }
                }
            }
        }

    }

    fun restartSearchWithCurrentQuery() {
        removeSearchList()
        searchVacancies()
    }

    private suspend fun buildVacancyFilter(page: Int): VacancyFilter {
        val queryText = _textFieldState.value.query
        val savedFilters = vacancyFilterStorageInteractor.getFilters().first()
        return savedFilters.copy(text = queryText, page = page)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText

        searchVacanciesDebounce(changedText)
        removeSearchList()
    }

    fun isFavorite(id: String): Boolean {
        val favorites = favoriteIds.value
        if (favorites.contains(id)) return true
        return vacanciesList.firstOrNull { it.id == id }?.isFavorite ?: false
    }

    fun onClearIcClick() {
        onLoadNextPageJob?.cancel()
        searchJob?.cancel()
        onQueryChange("")
        removeSearchList()
    }

    fun onLoadNextPage() {
        if (currentPage > maxPages) {
            return
        }
        onLoadNextPageJob?.cancel()
        renderSearchState(SearchState.Content(vacanciesList, true))
        currentPage++
        onLoadNextPageJob = viewModelScope.launch {
            val filter = buildVacancyFilter(page = currentPage)
            interactor.getVacancies(filter).collect { result ->
                when (result) {
                    is Result.Error -> {
                        renderSearchState(SearchState.Content(vacanciesList, false))
                        _paginationErrorMessage.value = result.message
                    }

                    is Result.Success<VacancyResponse> -> {
                        processResult(result.data)
                        renderSearchState(SearchState.Content(vacanciesList, false))
                    }
                }
            }
        }
    }

    fun onPaginationErrorShown() {
        _paginationErrorMessage.value = null
    }

    fun processResult(
        vacancyResponse: VacancyResponse = VacancyResponse(0, 0, 0, emptyList()),
        errorMessage: String = ""
    ) {
        currentPage = vacancyResponse.page
        maxPages = (vacancyResponse.pages - 1).coerceAtLeast(0)
        if (errorMessage.isEmpty()) {
            _foundVacancies.value = vacancyResponse.found
        }
        if (vacancyResponse.vacancies.isNotEmpty()) {
            val currentFavoriteIds = favoriteIds.value
            vacanciesList.addAll(vacancyResponse.vacancies.map {
                VacancyListItemUi(
                    it.id,
                    it.employer.logo,
                    it.name,
                    it.address?.city,
                    it.employer.name,
                    formatSalary(it.salary?.from, it.salary?.to, it.salary?.currency, resources),
                    currentFavoriteIds.contains(it.id)
                )
            })
        }
        when {
            errorMessage != "" -> {
                renderSearchState(
                    SearchState.Error(
                        message = errorMessage
                    )
                )
            }

            else -> {
                renderSearchState(
                    SearchState.Content(
                        vacanciesList
                    )
                )
            }
        }
    }

    private fun removeSearchList() {
        renderSearchState(SearchState.Nothing)
        vacanciesList.clear()
        currentPage = 0
        _foundVacancies.value = 0
    }

    private fun renderSearchState(state: SearchState) {
        _searchState.update { state }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _favoriteIds.value = result.data.map { it.id }.toSet()
                    }

                    is Result.Error -> {}
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val SUBSCRIBE_TIMEOUT = 5_000L
    }
}
