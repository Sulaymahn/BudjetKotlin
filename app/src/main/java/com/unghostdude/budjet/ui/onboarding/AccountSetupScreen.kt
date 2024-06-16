package com.unghostdude.budjet.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.supportedCurrencies
import com.unghostdude.budjet.viewmodel.AccountSetupScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSetupScreen(
    vm: AccountSetupScreenViewModel = hiltViewModel<AccountSetupScreenViewModel>(),
    onSetupComplete: () -> Unit
) {
    val context = LocalContext.current

    var showCurrencyMenu by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val focusManger = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .clickable(interactionSource = interactionSource, indication = null) {
                focusManger.clearFocus()
            }
    ) {
        Column {
            Text(
                text = context.getString(R.string.account_setup_title),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.displayMedium
            )

            Text(
                text = context.getString(R.string.account_setup_change_later_tip),
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Column {
            OutlinedTextField(
                value = vm.username.currentValue,
                onValueChange = { newValue ->
                    vm.username.setValue(newValue)
                },
                label = {
                    Text(text = context.getString(R.string.account_setup_username_label))
                },
                isError = !vm.username.isValid,
                supportingText = {
                    if (vm.username.errors.isNotEmpty()) {
                        Text(text = vm.username.errors.joinToString(separator = "\n"))
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.accountName.currentValue,
                onValueChange = { newValue ->
                    vm.accountName.setValue(newValue)
                },
                label = {
                    Text(text = context.getString(R.string.account_setup_account_name_label))
                },
                isError = !vm.accountName.isValid,
                supportingText = {
                    if (vm.accountName.errors.isNotEmpty()) {
                        Text(text = vm.accountName.errors.joinToString(separator = "\n"))
                    }
                },
                singleLine = true,
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
                        value = vm.currency?.displayName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text(text = context.getString(R.string.account_setup_account_currency_label))
                        },
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
                                    vm.currency = item
                                    showCurrencyMenu = !showCurrencyMenu
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                onSetupComplete()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = context.getString(R.string.done))
        }
    }
}