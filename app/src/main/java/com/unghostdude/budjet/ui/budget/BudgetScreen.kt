package com.unghostdude.budjet.ui.budget

import android.icu.number.NumberFormatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.viewmodel.BudgetScreenViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun BudgetScreen(
    vm: BudgetScreenViewModel = hiltViewModel<BudgetScreenViewModel>()
) {
    val budgets by vm.budgets.collectAsState(listOf())
    val formatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault())
    val numberFormatter =
        NumberFormatter.withLocale(LocalContext.current.resources.configuration.locales[0])

    LazyColumn(
        userScrollEnabled = true
    ) {
        items(items = budgets) { budget ->
            ListItem(
                headlineContent = {
                    Text(text = budget.budget.name)
                },
                supportingContent = {
                    Text(text = budget.budget.recurrence.toString())
                },
                trailingContent = {
                    Text(
                        text = "${budget.account.defaultCurrency.symbol} ${
                            numberFormatter.format(
                                budget.budget.amount
                            )
                        }"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                    }
            )
        }

        item {
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}