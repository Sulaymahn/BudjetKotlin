package com.unghostdude.budjet.ui.home

import android.icu.number.NumberFormatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.viewmodel.DashboardScreenViewModel
import java.text.NumberFormat

@Composable
fun DashboardScreen(
    account: AccountEntity,
    username: String,
    vm: DashboardScreenViewModel = hiltViewModel<DashboardScreenViewModel>()
) {
    vm.listen(account)

    val balance by vm.balance.collectAsState()
    val scrollState = rememberScrollState()
    val formatter =
        NumberFormatter.withLocale(LocalContext.current.resources.configuration.locales[0])
    val f =
        NumberFormat.getCurrencyInstance(LocalContext.current.resources.configuration.locales[0])
    f.currency = account.defaultCurrency

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .verticalScroll(scrollState)
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
            Box {
                Icon(
                    painter = painterResource(R.drawable.budjetlogo),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inversePrimary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .scale(2f)
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {

                    Text(text = "Balance")
                    Column {
                        Text(
                            //text = "${account.defaultCurrency.symbol} ${formatter.format(balance ?: 0)}",
                            text = f.format(balance ?: 0),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = username)
                            Text(text = account.name)
                        }
                    }
                }
            }
        }
    }
}