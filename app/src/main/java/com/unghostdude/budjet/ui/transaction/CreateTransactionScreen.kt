package com.unghostdude.budjet.ui.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.TransactionType
import com.unghostdude.budjet.viewmodel.transaction.CreateTransactionScreenViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionScreen(
    navigateAway: () -> Unit,
    vm: CreateTransactionScreenViewModel = hiltViewModel<CreateTransactionScreenViewModel>()
) {
    val categories by vm.categories.collectAsState()
    val accounts by vm.accounts.collectAsState()

    var dialog: CreateTransactionScreenDialog by remember {
        mutableStateOf(CreateTransactionScreenDialog.None)
    }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "Create transaction")
                },
                navigationIcon = {
                    IconButton(onClick = navigateAway) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = vm.canCreateTransaction(),
                        onClick = {
                            vm.createTransaction(
                                onCreated = navigateAway
                            )
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_done),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                focusManager.clearFocus()
            }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Transaction type
                MultiChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    SegmentedButton(
                        checked = vm.transactionType == TransactionType.Income,
                        onCheckedChange = {
                            vm.transactionType = TransactionType.Income
                        },
                        //shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        shape = RectangleShape
                    ) {
                        Text(text = "Income")
                    }

                    SegmentedButton(
                        checked = vm.transactionType == TransactionType.Expense,
                        onCheckedChange = {
                            vm.transactionType = TransactionType.Expense
                        },
                        //shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        shape = RectangleShape
                    ) {
                        Text(text = "Expense")
                    }
                }

                //
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                ) {
                    OutlinedTextField(
                        value = DateTimeFormatter
                            .ofLocalizedTime(FormatStyle.SHORT)
                            .withZone(ZoneId.systemDefault())
                            .format(vm.date),
                        label = {
                            Text(text = "Time")
                        },
                        readOnly = true,
                        onValueChange = {

                        },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { focus ->
                                if (focus.isFocused) {
                                    dialog = CreateTransactionScreenDialog.Time
                                }
                            }
                    )

                    if (dialog == CreateTransactionScreenDialog.Time) {
                        val tps = rememberTimePickerState(
                            initialHour = vm.date.hour,
                            initialMinute = vm.date.minute
                        )

                        Dialog(
                            onDismissRequest = {
                                focusManager.clearFocus()
                                dialog = CreateTransactionScreenDialog.None
                            }
                        ) {
                            Surface(
                                shape = DatePickerDefaults.shape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 20.dp),
                                        text = "Select time",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    TimePicker(state = tps)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Absolute.Right,
                                        modifier = Modifier
                                            .height(40.dp)
                                            .fillMaxWidth()
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                focusManager.clearFocus()
                                                dialog = CreateTransactionScreenDialog.None
                                            }) {
                                            Text(text = "Cancel")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                vm.date =
                                                    vm.date.withHour(tps.hour)
                                                        .withMinute(tps.minute)
                                                focusManager.clearFocus()
                                                dialog = CreateTransactionScreenDialog.None
                                            }) {
                                            Text(text = "Ok")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = DateTimeFormatter
                            .ofLocalizedDate(FormatStyle.MEDIUM)
                            .withZone(ZoneId.systemDefault())
                            .format(vm.date),
                        label = {
                            Text(text = "Date")
                        },
                        readOnly = true,
                        onValueChange = {

                        },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { focus ->
                                if (focus.isFocused) {
                                    dialog = CreateTransactionScreenDialog.Date
                                }
                            }
                    )

                    if (dialog == CreateTransactionScreenDialog.Date) {
                        val dps = rememberDatePickerState(
                            initialSelectedDateMillis = vm.date.toInstant(ZoneOffset.UTC)
                                .toEpochMilli(),
                            initialDisplayMode = DisplayMode.Input
                        )

                        DatePickerDialog(
                            onDismissRequest = {
                                focusManager.clearFocus()
                                dialog = CreateTransactionScreenDialog.None
                            },
                            confirmButton = {
                                Button(onClick = {
                                    val d = dps.selectedDateMillis?.let { d ->
                                        Instant.ofEpochMilli(d).atZone(
                                            ZoneId.systemDefault()
                                        )
                                    }
                                    if (d != null) {
                                        vm.date = vm.date
                                            .withYear(d.year)
                                            .withMonth(d.month.value)
                                            .withDayOfMonth(d.dayOfMonth)

                                        focusManager.clearFocus()
                                        dialog = CreateTransactionScreenDialog.None
                                    }
                                }) {
                                    Text(text = "Ok")
                                }
                            },
                            dismissButton = {
                                OutlinedButton(onClick = {
                                    focusManager.clearFocus()
                                    dialog = CreateTransactionScreenDialog.None
                                }) {
                                    Text(text = "Cancel")
                                }
                            }) {
                            DatePicker(state = dps)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ExposedDropdownMenuBox(
                        expanded = dialog == CreateTransactionScreenDialog.Account,
                        onExpandedChange = {
                            dialog =
                                if (dialog != CreateTransactionScreenDialog.Account) CreateTransactionScreenDialog.Account
                                else CreateTransactionScreenDialog.None
                        }
                    ) {
                        OutlinedTextField(
                            value = vm.account?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            label = {
                                Text(text = "Account*")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dialog == CreateTransactionScreenDialog.Account)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = dialog == CreateTransactionScreenDialog.Account,
                            onDismissRequest = {
                                dialog = CreateTransactionScreenDialog.None
                            }
                        ) {
                            accounts.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item.name) },
                                    onClick = {
                                        vm.account = item
                                        dialog = CreateTransactionScreenDialog.None
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = vm.title.currentValue,
                    label = {
                        Text(text = "Title")
                    },
                    onValueChange = { newValue ->
                        vm.title.setValue(newValue)
                    },
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    isError = !vm.title.isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    var amountText by remember {
                        mutableStateOf("")
                    }

                    OutlinedTextField(
                        value = amountText,
                        label = {
                            Text(text = "Amount*")
                        },
                        prefix = {
                            if(vm.account != null){
                                Text(text = vm.account?.currency?.symbol ?: "")
                            }
                        },
                        singleLine = true,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty()) {
                                amountText = newValue
                                vm.amount = null
                            } else if (!newValue.startsWith("0") && newValue.isDigitsOnly() && newValue.length <= 16) {
                                val num = newValue.toDoubleOrNull()
                                if (num != null) {
                                    amountText = newValue
                                    vm.amount = num
                                }
                            }
                        },
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Right)
                            }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = dialog == CreateTransactionScreenDialog.Category,
                            onExpandedChange = {
                                dialog =
                                    if (dialog != CreateTransactionScreenDialog.Category) CreateTransactionScreenDialog.Category
                                    else CreateTransactionScreenDialog.None
                            }
                        ) {
                            OutlinedTextField(
                                value = vm.category?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                label = {
                                    Text(text = "Category*")
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dialog == CreateTransactionScreenDialog.Category)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = dialog == CreateTransactionScreenDialog.Category,
                                onDismissRequest = {
                                    dialog = CreateTransactionScreenDialog.None
                                }
                            ) {
                                categories.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item.name) },
                                        onClick = {
                                            vm.category = item
                                            dialog = CreateTransactionScreenDialog.None
                                            focusManager.moveFocus(FocusDirection.Down)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


                OutlinedTextField(
                    value = vm.note.currentValue,
                    label = {
                        Text(text = "Note")
                    },
                    onValueChange = { newValue ->
                        vm.note.setValue(newValue)
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }
}

sealed class CreateTransactionScreenDialog {
    data object None : CreateTransactionScreenDialog()
    data object Category : CreateTransactionScreenDialog()
    data object Time : CreateTransactionScreenDialog()
    data object Date : CreateTransactionScreenDialog()
    data object Account : CreateTransactionScreenDialog()
}