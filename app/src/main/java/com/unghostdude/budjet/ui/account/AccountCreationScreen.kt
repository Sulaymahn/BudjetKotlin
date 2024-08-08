package com.unghostdude.budjet.ui.account

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.supportedCurrencies
import com.unghostdude.budjet.viewmodel.AccountCreationViewModel
import com.unghostdude.budjet.viewmodel.AccountSetupScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountCreationScreen(
    navigateAway: () -> Unit,
    vm: AccountCreationViewModel = hiltViewModel()
) {
    val focusManger = LocalFocusManager.current
    val interactionSource = remember {
        MutableInteractionSource()
    }
    var showCurrencyMenu by remember {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Create Account")
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
                        enabled = vm.isValid(),
                        onClick = {
                            vm.create(navigateAway)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_done),
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
                    value = vm.name.currentValue,
                    label = {
                        Text(text = "Name")
                    },
                    onValueChange = { newValue ->
                        vm.name.setValue(newValue)
                    },
                    keyboardActions = KeyboardActions(
                        onNext = {
//                        focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    isError = !vm.name.isValid,
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
                            value = vm.currency.currentValue,
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            label = {
                                Text(text = "Currency")
                            },
                            isError = !vm.currency.isValid,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCurrencyMenu) },
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
                                        vm.currency.setValue(item.currencyCode)
                                        showCurrencyMenu = !showCurrencyMenu
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = vm.startingAmount.currentValue,
                    label = {
                        Text(text = "Starting Amount")
                    },
                    onValueChange = { newValue ->
                        vm.startingAmount.setValue(newValue)
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
                            onClick = { vm.startingAmountIsExpense = !vm.startingAmountIsExpense },
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .width(110.dp)
                        ) {
                            Text(text = if (vm.startingAmountIsExpense) "Expense" else "Income")
                        }
                    },
                    isError = !vm.startingAmount.isValid,
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