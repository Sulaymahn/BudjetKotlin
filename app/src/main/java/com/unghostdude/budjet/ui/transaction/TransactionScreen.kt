package com.unghostdude.budjet.ui.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.viewmodel.TransactionsScreenViewModel
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TransactionScreen(
    vm: TransactionsScreenViewModel = hiltViewModel<TransactionsScreenViewModel>()
) {
    val transactions by vm.transactions.collectAsState(listOf())
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault())

    LazyColumn(
        userScrollEnabled = true
    ) {
        items(items = transactions) { transaction ->
            ListItem(
                headlineContent = {
                    Text(text = "${transaction.title}")
                },
                supportingContent = {
                    Text(text = formatter.format(transaction.date))
                },
                trailingContent = {
                    Text(text = "${transaction.currency.symbol} ${transaction.amount}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                    }
            )
        }
        
        item{
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}