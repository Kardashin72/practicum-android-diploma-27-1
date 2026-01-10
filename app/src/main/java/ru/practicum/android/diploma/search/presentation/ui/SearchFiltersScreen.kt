package ru.practicum.android.diploma.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.presentation.ui.theme.corner12
import ru.practicum.android.diploma.core.presentation.ui.theme.dp24
import ru.practicum.android.diploma.core.presentation.ui.theme.dp8
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchFiltersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFiltersScreen(
    onOpenIndustryFilter: () -> Unit,
    onBack: () -> Unit,
    onApply: () -> Unit,
    viewModel: SearchFiltersViewModel = koinViewModel(),
) {
    var salaryInput by rememberSaveable { mutableStateOf("") }
    val filters by viewModel.filters.collectAsState()
    val hideWithoutSalary = filters.onlyWithSalary == true

    LaunchedEffect(filters.salary) {
        if (salaryInput.isEmpty() && filters.salary != null) {
            salaryInput = filters.salary.toString()
        }
    }

    val hasActiveFilters =
        filters.salary != null ||
            filters.onlyWithSalary == true ||
            filters.industry != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_filters),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                FilterRow(
                    title = stringResource(id = R.string.filter_industry),
                    value = filters.industryName,
                    onClick = onOpenIndustryFilter,
                    onClear = { viewModel.clearIndustry() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                SalaryField(
                    value = salaryInput,
                    onValueChange = { newValue ->
                        val digitsOnly = newValue.filter { it.isDigit() }
                        salaryInput = digitsOnly
                        viewModel.onSalaryChanged(digitsOnly)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.filter_hide_without_salary),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = hideWithoutSalary,
                        onCheckedChange = { checked ->
                            viewModel.onOnlyWithSalaryChanged(checked)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.primary,
                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (hasActiveFilters) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dp24),
                        verticalArrangement = Arrangement.spacedBy(dp8),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(corner12),
                            onClick = onApply
                        ) {
                            Text(text = stringResource(id = R.string.filter_apply))
                        }

                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                salaryInput = ""
                                viewModel.resetFilters()
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.filter_reset),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    title: String,
    value: String?,
    onClick: () -> Unit,
    onClear: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val titleColor =
            if (value != null) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = titleColor
            )
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        IconButton(
            onClick = if (value != null && onClear != null) onClear else onClick,
            modifier = Modifier.size(24.dp)
        ) {
            val iconRes =
                if (value != null && onClear != null) {
                    R.drawable.close_24px
                } else {
                    R.drawable.ic__arrow_forward_24px
                }

            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SalaryField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(corner12)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val labelColor = when {
                        isFocused -> MaterialTheme.colorScheme.primary
                        !isFocused && value.isEmpty() -> MaterialTheme.colorScheme.outline
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Text(
                        text = stringResource(id = R.string.filter_expected_salary_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = labelColor
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.filter_expected_salary_hint),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            innerTextField()
                        }
                    }
                }

                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}
