package com.unghostdude.budjet.ui.onboarding

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.supportedCurrencies
import com.unghostdude.budjet.viewmodel.AccountSetupScreenState
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

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = context.getString(R.string.account_setup_title)
                    )
                },
                actions = {
                    IconButton(
                        enabled = vm.canSetupAccount(),
                        onClick = {
                        vm.setupAccount(
                            onSetupComplete
                        )
                    }) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, indication = null) {
                    focusManger.clearFocus()
                }
        ) {
            if (vm.state == AccountSetupScreenState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
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
                            Text(text = vm.username.errors.first())
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
                            Text(text = vm.accountName.errors.first())
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
                            readOnly = vm.state == AccountSetupScreenState.Loading,
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
                
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = context.getString(R.string.account_setup_change_later_tip),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}