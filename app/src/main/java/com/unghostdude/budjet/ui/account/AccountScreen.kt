package com.unghostdude.budjet.ui.account

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.util.Currency
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.viewmodel.account.AccountScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccountScreen(
    navigateAway: () -> Unit,
    navigateToAccountCreation: () -> Unit,
    navigateToAccountDetail: (id: String) -> Unit,
    vm: AccountScreenViewModel = hiltViewModel()
) {
    val accounts by vm.accounts.collectAsState()

    val currencyFormatter = NumberFormatter.with()
        .notation(Notation.simple())
        .precision(Precision.currency(Currency.CurrencyUsage.STANDARD))
        .locale(java.util.Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Accounts")
                },
                navigationIcon = {
                    IconButton(onClick = { navigateAway() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = navigateToAccountCreation) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
        ) {
            items(items = accounts) { account ->
                ListItem(
                    headlineContent = {
                        Text(text = account.name)
                    },
                    supportingContent = {
                        Text(
                            text = currencyFormatter
                                .unit(Currency.getInstance(account.currency.currencyCode))
                                .format(account.balance + account.startAmount)
                                .toString()
                        )
                    },
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                navigateToAccountDetail(account.id.toString())
                            }
                        )
                )
            }
        }
    }
}