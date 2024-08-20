package com.unghostdude.budjet.ui.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.BudgetCycle
import com.unghostdude.budjet.ui.CategorySelectionDialog
import com.unghostdude.budjet.ui.transaction.CreateTransactionScreenDialog
import com.unghostdude.budjet.viewmodel.budget.BudgetCreationScreenViewModel

enum class BudgetCreationScreenDialog {
    None,
    Category,
    Account
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetCreationScreen(
    navigateAway: () -> Unit,
    vm: BudgetCreationScreenViewModel = hiltViewModel<BudgetCreationScreenViewModel>()
) {
    val focusManager = LocalFocusManager.current
    var dialog by remember {
        mutableStateOf(BudgetCreationScreenDialog.None)
    }
    val scaffoldISource = remember {
        MutableInteractionSource()
    }

    val categories by vm.categories.collectAsState()
    val accounts by vm.accounts.collectAsState()
    var selectedCategories by remember {
        mutableStateOf<List<Int>>(listOf())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Create Budget")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateAway()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = vm.canCreateBudget(),
                        onClick = {
                            vm.createBudget(
                                selectedCategories = selectedCategories.ifEmpty { categories.map { it.id } },
                                callback = navigateAway
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_done),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .clickable(scaffoldISource, null) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
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
                            focusManager.moveFocus(FocusDirection.Down)
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
                        expanded = dialog == BudgetCreationScreenDialog.Account,
                        onExpandedChange = {
                            dialog =
                                if (dialog != BudgetCreationScreenDialog.Account) BudgetCreationScreenDialog.Account
                                else BudgetCreationScreenDialog.None
                        }
                    ) {
                        OutlinedTextField(
                            value = vm.account?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            label = {
                                Text(text = "Account")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dialog == BudgetCreationScreenDialog.Account)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = dialog == BudgetCreationScreenDialog.Account,
                            onDismissRequest = {
                                dialog = BudgetCreationScreenDialog.None
                            }
                        ) {
                            accounts.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item.name) },
                                    onClick = {
                                        vm.account = item
                                        dialog = BudgetCreationScreenDialog.None
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                var amountText by remember {
                    mutableStateOf("")
                }

                OutlinedTextField(
                    value = amountText,
                    label = {
                        Text(text = "Amount")
                    },
                    singleLine = true,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty()) {
                            amountText = newValue
                            vm.amount = 0.0
                        } else if (!newValue.startsWith("0") && newValue.isDigitsOnly() && newValue.length <= 16) {
                            val num = newValue.toDoubleOrNull()
                            if (num != null) {
                                amountText = newValue
                                vm.amount = num
                            }
                        }
                    },
                    prefix = {
                        if (vm.account != null) {
                            Text(text = vm.account!!.currency.symbol)
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
                        .fillMaxWidth()
                )



                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (vm.cycle != BudgetCycle.OneTime) {
                        var cycleSize by remember {
                            mutableStateOf(vm.cycleSize.toString())
                        }

                        OutlinedTextField(
                            value = cycleSize,
                            label = {
                                Text(text = "Period")
                            },
                            singleLine = true,
                            onValueChange = { newValue ->
                                if (!newValue.startsWith("0") && newValue.length < 8) {
                                    val n = newValue.toLongOrNull()
                                    vm.cycleSize = n ?: 0
                                    cycleSize = newValue
                                }
                            },
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Right)
                                }
                            ),
                            isError = vm.cycleSize < 1,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .weight(1f)
                        )
                    }


                    var showCycleDropdown by remember {
                        mutableStateOf(false)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = showCycleDropdown,
                            onExpandedChange = {
                                showCycleDropdown = true
                            }
                        ) {
                            OutlinedTextField(
                                value = vm.cycle?.name ?: "",
                                onValueChange = {

                                },
                                readOnly = true,
                                singleLine = true,
                                label = {
                                    Text(text = "Cycle")
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCycleDropdown)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = showCycleDropdown,
                                onDismissRequest = {
                                    showCycleDropdown = false
                                }
                            ) {
                                BudgetCycle.entries.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item.name) },
                                        onClick = {
                                            vm.cycle = item
                                            showCycleDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Card(
                    shape = OutlinedTextFieldDefaults.shape,
                    modifier = Modifier
                        .height(OutlinedTextFieldDefaults.MinHeight)
                        .fillMaxWidth()
                        .clickable {
                            dialog = BudgetCreationScreenDialog.Category
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = if (selectedCategories.isNotEmpty() && selectedCategories.size < categories.size) {
                                if (selectedCategories.size == 1) {
                                    "1 Category"
                                } else {
                                    "${selectedCategories.size} Categories"
                                }
                            } else {
                                "All Categories"
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(
                                    start = OutlinedTextFieldDefaults
                                        .contentPadding()
                                        .calculateLeftPadding(LayoutDirection.Ltr)
                                )
                        )
                    }
                }
            }
        }

        if (dialog == BudgetCreationScreenDialog.Category) {
            CategorySelectionDialog(
                initialSelected = selectedCategories,
                onSelected = { id ->
                    selectedCategories = selectedCategories.plus(id)
                },
                onDeselected = { id ->
                    selectedCategories = selectedCategories.minus(id)
                },
                onDismiss = {
                    dialog = BudgetCreationScreenDialog.None
                }
            )
        }
    }
}