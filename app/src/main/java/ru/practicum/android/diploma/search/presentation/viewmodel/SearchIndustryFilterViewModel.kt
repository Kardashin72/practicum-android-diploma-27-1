package ru.practicum.android.diploma.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.model.FilterIndustry

class SearchIndustryFilterViewModel(
    private val interactor: SearchInteractor,
) : ViewModel() {

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }

    private val _industries = MutableStateFlow<List<FilterIndustry>>(emptyList())
    val industries: StateFlow<List<FilterIndustry>> = _industries.asStateFlow()

    // Состояние загрузки индустрий
    private val _isLoadingIndustries = MutableStateFlow(true)
    val isLoadingIndustries: StateFlow<Boolean> = _isLoadingIndustries.asStateFlow()

    // Состояние ошибки загрузки
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Выбранная отрасль
    private val _selectedIndustryId = MutableStateFlow<Int?>(null)
    val selectedIndustryId: StateFlow<Int?> = _selectedIndustryId.asStateFlow()

    // Поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Событие навигации назад
    private val _navigateBack = MutableStateFlow(false)
    val navigateBack: StateFlow<Boolean> = _navigateBack.asStateFlow()

    // Отфильтрованный список отраслей
    val filteredIndustries: StateFlow<List<FilterIndustry>> = combine(
        _searchQuery,
        _industries
    ) { query, industries ->
        if (query.isBlank()) {
            industries
        } else {
            industries.filter { industry ->
                industry.name.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    init {
        loadIndustries()
    }

    private fun loadIndustries() {
        viewModelScope.launch {
            _isLoadingIndustries.value = true
            _errorMessage.value = null
            try {
                val result = interactor.getIndustry().first()
                when (result) {
                    is ru.practicum.android.diploma.search.domain.model.Result.Success -> {
                        _industries.value = result.data
                        _errorMessage.value = null
                    }

                    is ru.practicum.android.diploma.search.domain.model.Result.Error -> {
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: NoSuchElementException) {
                _errorMessage.value = "Неизвестная ошибка"
                e.printStackTrace()
            } catch (e: RuntimeException) {
                _errorMessage.value = "Неизвестная ошибка"
                e.printStackTrace()
            } finally {
                _isLoadingIndustries.value = false
            }
        }
    }

    fun onIndustryClick(industryId: Int) {
        onIndustrySelected(industryId)
    }

    fun onIndustrySelected(industryId: Int) {
        _selectedIndustryId.value = industryId
    }

    fun clearSelection() {
        _selectedIndustryId.value = null
    }

    fun onSearchQueryChange(text: String) {
        _searchQuery.value = text
    }

    fun onApplyClick() {
        _navigateBack.value = true
    }

    fun onNavigateBackHandled() {
        _navigateBack.value = false
    }
}
