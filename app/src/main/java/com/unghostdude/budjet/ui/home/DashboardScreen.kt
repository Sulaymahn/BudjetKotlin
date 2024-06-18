package com.unghostdude.budjet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.viewmodel.DashboardScreenViewModel

@Composable
fun DashboardScreen(
    account: Account,
    vm: DashboardScreenViewModel = hiltViewModel<DashboardScreenViewModel>()
) {
    val balance by vm.balance(account.id).collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(text = "Balance")
                Column {
                    Text(
                        text = "${account.defaultCurrency.symbol} $balance",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = account.name)
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(100.dp)
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {

            }
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {

            }
        }
    }
}