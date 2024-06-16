package com.unghostdude.budjet.ui.transaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.viewmodel.TransactionsScreenViewModel

@Composable
fun TransactionScreen(
    vm: TransactionsScreenViewModel = hiltViewModel<TransactionsScreenViewModel>(),
    navigateToNewTransactionScreen: () -> Unit
){
    val transactions by vm.transactions.collectAsState(listOf())

    LazyColumn {
        item{
            Button(onClick = navigateToNewTransactionScreen) {
                Text(text = "Add")
            }
        }

        items(items = transactions){ transaction ->
            Text(text = transaction.title.toString())
        }
    }
}