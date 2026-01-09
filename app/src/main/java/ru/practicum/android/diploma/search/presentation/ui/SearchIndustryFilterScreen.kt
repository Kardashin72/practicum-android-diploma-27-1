package ru.practicum.android.diploma.search.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.presentation.ui.components.Loading
import ru.practicum.android.diploma.core.presentation.ui.components.PlaceHolder
import ru.practicum.android.diploma.core.presentation.ui.theme.CustomTypography
import ru.practicum.android.diploma.core.presentation.ui.theme.blue
import ru.practicum.android.diploma.core.presentation.ui.theme.grey
import ru.practicum.android.diploma.core.presentation.ui.theme.lightGrey
import ru.practicum.android.diploma.core.presentation.ui.theme.white
import ru.practicum.android.diploma.search.domain.model.FilterIndustry
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchIndustryFilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchIndustryFilterScreen(
    onBack: () -> Unit,
    onIndustrySelected: (FilterIndustry) -> Unit = {},
    selectedIndustryId: Int? = null,
    viewModel: SearchIndustryFilterViewModel,
) {
    val industries by viewModel.industries.collectAsState()
    val isLoading by viewModel.isLoadingIndustries.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedIndustryIdFromViewModel by viewModel.selectedIndustryId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredIndustries by viewModel.filteredIndustries.collectAsState()
    val navigateBack by viewModel.navigateBack.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Инициализируем выбранную отрасль в ViewModel
    LaunchedEffect(selectedIndustryId) {
        if (selectedIndustryId != null && selectedIndustryIdFromViewModel == null) {
            viewModel.onIndustrySelected(selectedIndustryId)
        }
    }

    // Используем выбранную отрасль из ViewModel
    val currentSelectedId = selectedIndustryIdFromViewModel ?: selectedIndustryId

    val selectedIndustry = industries.find { it.id == currentSelectedId }

    // Обработка события навигации назад
    LaunchedEffect(navigateBack) {
        if (navigateBack) {
            selectedIndustry?.let { onIndustrySelected(it) }
            onBack()
            viewModel.onNavigateBackHandled()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_industry_filter),
                        style = CustomTypography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )

            when {
                isLoading -> {
                    Loading(Modifier.fillMaxSize())
                }

                errorMessage != null -> {
                    val drawableId = when (errorMessage) {
                        stringResource(R.string.error_no_internet) ->
                            R.drawable.internet_connection_error_placeholder

                        stringResource(R.string.error_server) ->
                            R.drawable.search_server_error_placeholder

                        stringResource(R.string.error_poor_connection) ->
                            R.drawable.internet_connection_error_placeholder

                        else ->
                            R.drawable.get_items_error_placeholder
                    }
                    val placeholderMessageId = when (drawableId) {
                        R.drawable.internet_connection_error_placeholder ->
                            R.string.error_no_internet

                        R.drawable.search_server_error_placeholder ->
                            R.string.error_server

                        else ->
                            R.string.get_vacancies_error
                    }
                    PlaceHolder(
                        drawableId,
                        placeholderMessageId
                    )
                }

                industries.isEmpty() -> {
                    PlaceHolder(
                        R.drawable.empty_favorites_placeholder,
                        R.string.favorites_list_empty
                    )
                }

                else -> {
                    IndustrySearchField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        onClear = { viewModel.onSearchQueryChange("") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    if (searchQuery.isNotEmpty() && filteredIndustries.isEmpty()) {
                        LaunchedEffect(searchQuery, filteredIndustries) {
                            keyboardController?.hide()
                        }
                        PlaceHolder(
                            R.drawable.get_items_error_placeholder,
                            R.string.industry_not_found,
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(
                                items = filteredIndustries,
                                key = { it.id }
                            ) { industry ->
                                IndustryItem(
                                    industry = industry,
                                    selected = industry.id == currentSelectedId,
                                    onSelect = {
                                        keyboardController?.hide()
                                        viewModel.onIndustryClick(industry.id)
                                    }
                                )
                            }
                        }

                        if (selectedIndustry != null) {
                            Button(
                                onClick = {
                                    viewModel.onApplyClick()
                                },

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(60.dp),

                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = blue, // Цвет фона кнопки
                                    contentColor = white    // Цвет текста на кнопке
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.select),
                                    style = CustomTypography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndustrySearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    keyboardController?.show()
                } else {
                    keyboardController?.hide()
                }
            },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.industry_search_hint),
                color = grey,
                style = CustomTypography.titleMedium
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        painter = painterResource(R.drawable.close_24px),
                        contentDescription = "Очистить",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Icon(
                    painter = painterResource(R.drawable.search_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = lightGrey,
            focusedContainerColor = lightGrey,
            cursorColor = MaterialTheme.colorScheme.primary,
            selectionColors = TextSelectionColors(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary
            )
        ),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun IndustryItem(
    industry: FilterIndustry,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = industry.name,
            style = CustomTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
        )
        RadioButton(
            selected = selected,
            onClick = onSelect,
            modifier = Modifier.padding(start = 8.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = blue,
                unselectedColor = blue
            )
        )
    }
}
