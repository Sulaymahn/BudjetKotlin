package com.unghostdude.budjet.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.AccountForUpdate
import com.unghostdude.budjet.model.supportedCurrencies
import com.unghostdude.budjet.viewmodel.account.AccountDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    accountId: String,
    navigateAway: () -> Unit,
    vm: AccountDetailViewModel = hiltViewModel()
) {
    val account by vm.account.collectAsState()

    LaunchedEffect(Unit) {
        vm.fetch(accountId)
    }

    val focusManger = LocalFocusManager.current
    val interactionSource = remember {
        MutableInteractionSource()
    }
    var showCurrencyMenu by remember {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()

    if (account != null) {
        var accountForUpdate: AccountForUpdate? by remember {
            mutableStateOf(
                AccountForUpdate(
                    id = account!!.id,
                    name = account!!.name,
                    currency = account!!.currency,
                    balance = account!!.balance + account!!.startAmount
                )
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Edit Account")
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateAway) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            enabled = accountForUpdate != null,
                            onClick = {
                                vm.updateAccount(
                                    accountUpdate = accountForUpdate!!,
                                    onComplete = {
                                        navigateAway()
                                    }
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_done),
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {
                                vm.deleteAccount {
                                    navigateAway()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_delete),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { padding ->

            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .clickable(interactionSource = interactionSource, indication = null) {
                        focusManger.clearFocus()
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState)
                ) {
                    OutlinedTextField(
                        value = accountForUpdate!!.name,
                        label = {
                            Text(text = "Name")
                        },
                        onValueChange = { newValue ->
                            accountForUpdate = accountForUpdate!!.copy(name = newValue)
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = showCurrencyMenu,
                            onExpandedChange = {
                                showCurrencyMenu = !showCurrencyMenu
                            }
                        ) {
                            OutlinedTextField(
                                value = accountForUpdate!!.currency.currencyCode,
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                label = {
                                    Text(text = "Currency")
                                },
                                trailingIcon = { TrailingIcon(expanded = showCurrencyMenu) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = showCurrencyMenu,
                                onDismissRequest = { showCurrencyMenu = false }
                            ) {
                                supportedCurrencies.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item.displayName) },
                                        onClick = {
                                            accountForUpdate =
                                                accountForUpdate!!.copy(currency = item)
                                            showCurrencyMenu = !showCurrencyMenu
                                        }
                                    )
                                }
                            }
                        }
                    }

                    var newBalance by remember {
                        mutableStateOf(accountForUpdate!!.balance.toString())
                    }

                    OutlinedTextField(
                        value = newBalance,
                        label = {
                            Text(text = "Current Balance")
                        },
                        onValueChange = { newValue ->
                            val amount = newValue.toDoubleOrNull()
                            if (amount != null) {
                                accountForUpdate = accountForUpdate!!.copy(balance = amount)
                                newBalance = newValue
                            } else if (newValue.isBlank()) {
                                accountForUpdate = accountForUpdate!!.copy(balance = 0.0)
                                newBalance = newValue
                            }
                        },
                        keyboardActions = KeyboardActions(
                            onNext = {
//                        focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        singleLine = true,
                        trailingIcon = {
                            OutlinedButton(
                                shape = RectangleShape,
                                onClick = {
                                    //vm.startingAmountIsExpense = !vm.startingAmountIsExpense
                                },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .width(110.dp)
                            ) {
                                //Text(text = if (vm.startingAmountIsExpense) "Expense" else "Income")
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}