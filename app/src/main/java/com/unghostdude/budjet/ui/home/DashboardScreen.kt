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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.viewmodel.DashboardScreenViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    username: String,
    navigateToAccountScreen: () -> Unit,
    vm: DashboardScreenViewModel = hiltViewModel<DashboardScreenViewModel>()
) {
    var dialog by remember {
        mutableStateOf(DashboardDialog.None)
    }
    val accountWithBalance by vm.selectedAccount.collectAsState()
    val accounts by vm.accounts.collectAsState()
    val scrollState = rememberScrollState()
    val activeAccount by vm.selectedAccount.collectAsState()

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
            switchSelectedAccount = {
                dialog = DashboardDialog.SwitchAccount
            },
            formatter = currencyFormatter
        )
        AccountsCard(
            accounts = accounts,
            navigateToAccountScreen = navigateToAccountScreen,
            formatter = currencyFormatter
        )
    }

    if (dialog == DashboardDialog.SwitchAccount) {
        ModalBottomSheet(
            onDismissRequest = {
                dialog = DashboardDialog.None
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Selected Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            accounts.forEach { account ->
                ListItem(
                    headlineContent = {
                        Text(text = account.name)
                    },
                    supportingContent = {
                        Text(text = account.currency.displayName)
                    },
                    trailingContent = {
                        if (account.id == activeAccount?.id) {
                            Text(text = "active")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (account.id != activeAccount?.id) {
                                vm.switchAccount(account)
                            }

                            dialog = DashboardDialog.None
                        }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

enum class DashboardDialog {
    None,
    SwitchAccount
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
                        val balance = account.balance + account.startAmount
                        Text(
                            text = formatter
                                .unit(Currency.getInstance(account.currency.currencyCode))
                                .format(abs(balance))
                                .toString(),
                            style = MaterialTheme.typography.displaySmall,
                            color = if (balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
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

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            repeat(accounts.take(3).size) { i ->
                val account = accounts[i]
                val balance = account.balance + account.startAmount

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
                            .format(abs(balance))
                            .toString(),
                        color = if (balance < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}