package com.unghostdude.budjet.ui.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.Recurrence
import com.unghostdude.budjet.viewmodel.BudgetCreationScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BudgetCreationScreen(
    account: AccountEntity,
    navigateAway: () -> Unit,
    vm: BudgetCreationScreenViewModel = hiltViewModel<BudgetCreationScreenViewModel>()
) {
    val categories by vm.categories.collectAsState()
    val focusManager = LocalFocusManager.current
    val scaffoldISource = remember {
        MutableInteractionSource()
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
                            vm.createBudget(account, navigateAway)
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var amountText by remember {
                        mutableStateOf("")
                    }

                    OutlinedTextField(
                        value = amountText,
                        label = {
                            Text(text = "Amount*")
                        },
                        prefix = {
                            Text(text = account.currency.symbol)
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
                                    Text(text = "Category*")
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
                                Recurrence.entries.forEach { item ->
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

                Column {
                    Text(text = "Categories")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FilterChip(
                            selected = false,
                            onClick = {
                                vm.selectedCategories = if (vm.selectedCategories.isEmpty()) {
                                    categories
                                } else {
                                    listOf()
                                }
                            },
                            label = { Text(text = "All") }
                        )

                        repeat(categories.size) { index ->
                            FilterChip(
                                selected = vm.selectedCategories.contains(categories[index]),
                                onClick = {
                                    vm.selectedCategories = if (vm.selectedCategories.contains(categories[index])) {
                                        vm.selectedCategories.minus(categories[index])
                                    } else {
                                        vm.selectedCategories.plus(categories[index])
                                    }
                                },
                                label = {
                                    Text(text = categories[index].name)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}