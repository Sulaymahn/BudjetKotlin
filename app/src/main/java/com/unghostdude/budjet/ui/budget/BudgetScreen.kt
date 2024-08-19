package com.unghostdude.budjet.ui.budget

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.icu.util.MeasureUnit
import android.icu.util.ULocale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.viewmodel.budget.BudgetScreenViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun BudgetScreen(
    navigateToBudgetDetail: (String) -> Unit,
    vm: BudgetScreenViewModel = hiltViewModel<BudgetScreenViewModel>()
) {
    val budgets by vm.budgets.collectAsState(listOf())
    val amountFormatter = NumberFormatter
        .withLocale(Locale.getDefault())
        .precision(Precision.maxFraction(2))

    if (budgets.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(text = "You haven't made any budget yet")
        }
    } else {
        LazyColumn(
            userScrollEnabled = true
        ) {
            items(items = budgets) { budget ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigateToBudgetDetail(budget.budget.id.toString())
                        }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()

                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = budget.budget.name
                            )
                            Text(text = budget.budget.cycle.name)
                        }

                        LinearProgressIndicator(
                            progress = {
                                (budget.currentAmount / budget.budget.amount).toFloat()
                            },
                            color = if (budget.currentAmount > budget.budget.amount) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            strokeCap = StrokeCap.Square,
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text =
                                "${
                                    amountFormatter
                                        .notation(Notation.compactShort())
                                        .unit(Currency.getInstance(budget.account.currency.currencyCode))
                                        .format(budget.currentAmount)
                                }" + "/" + "${
                                    amountFormatter
                                        .format(budget.budget.amount)
                                }",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = NumberFormatter.with()
                                    .unit(MeasureUnit.PERCENT)
                                    .precision(Precision.fixedFraction(2))
                                    .locale(ULocale.getDefault())
                                    .format(budget.currentAmount / budget.budget.amount * 100)
                                    .toString(),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}