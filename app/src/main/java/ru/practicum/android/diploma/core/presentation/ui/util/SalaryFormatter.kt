package ru.practicum.android.diploma.core.presentation.ui.util

import android.content.res.Resources
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.search.domain.model.Salary
import java.text.NumberFormat
import java.util.Locale

fun formatSalary(
    from: Int?,
    to: Int?,
    currency: String?,
    resources: Resources,
): String? {
    if (from == null && to == null) return null

    val currencySymbol = currency.toCurrencySymbol()
    val numberFormat = NumberFormat.getIntegerInstance(Locale("ru", "RU"))

    return when {
        from != null && to != null -> {
            val formattedFrom = numberFormat.format(from)
            val formattedTo = numberFormat.format(to)
            resources.getString(R.string.salary_from_to, formattedFrom, formattedTo, currencySymbol)
        }

        from != null -> {
            val formattedFrom = numberFormat.format(from)
            resources.getString(R.string.salary_from, formattedFrom, currencySymbol)
        }

        to != null -> {
            val formattedTo = numberFormat.format(to)
            resources.getString(R.string.salary_to, formattedTo, currencySymbol)
        }

        else -> null
    }
}

fun Salary?.format(resources: Resources): String? =
    this?.let {
        formatSalary(
            from = it.from,
            to = it.to,
            currency = it.currency,
            resources = resources,
        )
    }

private fun String?.toCurrencySymbol(): String =
    when (this) {
        "RUR", "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "HKD" -> "HK$"
        "AUD" -> "A$"
        "SGD" -> "S$"
        "NZD" -> "NZ$"
        "SEK" -> "SEK"
        else -> this.orEmpty()
    }
