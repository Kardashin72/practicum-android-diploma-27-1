package ru.practicum.android.diploma.search.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.core.presentation.ui.theme.VacancySearchAppTheme
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchFiltersViewModel
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchIndustryFilterViewModel

class SearchIndustryFilterFragment : Fragment() {
    private val viewModel by viewModel<SearchIndustryFilterViewModel>()
    private val filtersViewModel by activityViewModel<SearchFiltersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                VacancySearchAppTheme {
                    val selectedIndustryId by filtersViewModel.filters.collectAsState()

                    SearchIndustryFilterScreen(
                        onBack = { navigateBack() },
                        onIndustrySelected = { industry ->
                            onIndustrySelected(industry)
                        },
                        selectedIndustryId = selectedIndustryId.industry,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    private fun onIndustrySelected(industry: ru.practicum.android.diploma.search.domain.model.FilterIndustry) {
//        filtersViewModel.onIndustrySelected(industry)
    }

    private fun navigateBack() {
        if (isAdded && view != null) {
            findNavController().navigateUp()
        }
    }
}
