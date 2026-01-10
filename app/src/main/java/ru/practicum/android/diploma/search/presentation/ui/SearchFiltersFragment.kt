package ru.practicum.android.diploma.search.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.presentation.ui.theme.VacancySearchAppTheme
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchViewModel

class SearchFiltersFragment : Fragment() {
    private val searchViewModel by activityViewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                VacancySearchAppTheme {
                    MaterialTheme {
                        SearchFiltersScreen(
                            onBack = { navigateBack() },
                            onOpenIndustryFilter = { openIndustryFilter() },
                            onApply = { applyFiltersAndSearch() },
                        )
                    }
                }
            }
        }
    }

    private fun openIndustryFilter() {
        findNavController().navigate(R.id.searchIndustryFilterFragment)
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun applyFiltersAndSearch() {
        val query = searchViewModel.textFieldState.value.query
        if (query.isNotEmpty()) {
            searchViewModel.restartSearchWithCurrentQuery()
        }
        navigateBack()
    }
}
