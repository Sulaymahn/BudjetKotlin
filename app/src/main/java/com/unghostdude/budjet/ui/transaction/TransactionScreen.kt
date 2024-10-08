package com.unghostdude.budjet.ui.transaction

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.util.Currency
import android.icu.util.ULocale
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.model.TransactionType
import com.unghostdude.budjet.viewmodel.transaction.TransactionsScreenViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionScreen(
    navigateToTransactionDetail: (id: String) -> Unit,
    vm: TransactionsScreenViewModel = hiltViewModel<TransactionsScreenViewModel>()
) {
    val transactionGroups by vm.transactions.collectAsState()

    val dateFormatter =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withZone(ZoneId.systemDefault())

    val currencyFormatter =
        NumberFormatter
            .withLocale(ULocale.getDefault())
            .notation(Notation.compactShort())

    if (transactionGroups.isNotEmpty()) {
        LazyColumn(
            userScrollEnabled = true
        ) {
            repeat(transactionGroups.size) { index ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = when (transactionGroups[index].date) {
                                LocalDate.now() -> "Today"
                                LocalDate.now().minusDays(1) -> "Yesterday"
                                else -> transactionGroups[index].date.format(dateFormatter)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(16.dp)
                        )

                        HorizontalDivider()
                    }

                }
                items(items = transactionGroups[index].transactions) { transaction ->
                    ListItem(
                        headlineContent = {
                            if (transaction.title.isNotBlank()) {
                                Text(text = "${transaction.title} - ${transaction.category.name}")
                            } else {
                                Text(text = transaction.category.name)
                            }

                        },
                        supportingContent = {
                            Text(text = transaction.account.name)
                        },
                        trailingContent = {
                            Text(
                                text = currencyFormatter
                                    .unit(Currency.getInstance(transaction.currency.currencyCode))
                                    .format(transaction.amount)
                                    .toString(),
                                color = if (transaction.type == TransactionType.Expense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    navigateToTransactionDetail(transaction.id.toString())
                                },
                                onLongClick = {

                                }
                            )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(text = "You haven't made any transactions yet")
        }
    }
}