package com.unghostdude.budjet.ui.home

import android.icu.number.LocalizedNumberFormatter
import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.util.Currency
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.viewmodel.DashboardScreenViewModel

@Composable
fun DashboardScreen(
    username: String,
    navigateToAccountScreen: () -> Unit,
    navigateToSwitchSelectedAccount: () -> Unit,
    vm: DashboardScreenViewModel = hiltViewModel<DashboardScreenViewModel>()
) {
    val accountWithBalance by vm.selectedAccount.collectAsState()
    val accounts by vm.accounts.collectAsState()
    val scrollState = rememberScrollState()

    val currencyFormatter = NumberFormatter.with()
        .notation(Notation.simple())
        .precision(Precision.currency(Currency.CurrencyUsage.STANDARD))
        .locale(java.util.Locale.getDefault())

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        SelectedAccountBalanceCard(
            username = username,
            account = accountWithBalance,
            switchSelectedAccount = navigateToSwitchSelectedAccount,
            formatter = currencyFormatter
        )
        AccountsCard(
            accounts = accounts,
            navigateToAccountScreen = navigateToAccountScreen,
            formatter = currencyFormatter
        )
    }
}

@Composable
fun SelectedAccountBalanceCard(
    username: String,
    account: Account?,
    formatter: LocalizedNumberFormatter,
    switchSelectedAccount: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(CardDefaults.shape)
            .clickable {
                switchSelectedAccount()
            }
    ) {
        Box {
            Icon(
                painter = painterResource(R.drawable.budjetlogo),
                contentDescription = null,
                tint = Color.White.copy(
                    alpha = 0.25f
                ),
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
                Text(
                    text = "Balance"
                )
                Column {
                    if (account != null) {
                        Text(
                            text = formatter
                                .unit(Currency.getInstance(account.currency.currencyCode))
                                .format(account.balance + account.startAmount)
                                .toString(),
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

@Composable
fun AccountsCard(
    accounts: List<Account>,
    formatter: LocalizedNumberFormatter,
    navigateToAccountScreen: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clip(CardDefaults.shape)
            .clickable {
                navigateToAccountScreen()
            }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Accounts",
                    style = MaterialTheme.typography.titleMedium
                )

                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            repeat(accounts.take(3).size) { i ->
                val account = accounts[i]

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = formatter
                            .unit(Currency.getInstance(account.currency.currencyCode))
                            .format(account.balance + account.startAmount)
                            .toString()
                    )
                }
            }
        }
    }
}