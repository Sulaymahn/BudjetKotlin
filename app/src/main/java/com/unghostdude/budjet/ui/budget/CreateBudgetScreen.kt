package com.unghostdude.budjet.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.Category
import com.unghostdude.budjet.viewmodel.CreateBudgetScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateBudgetScreen(
    account: Account,
    navigateAway: () -> Unit,
    vm: CreateBudgetScreenViewModel = hiltViewModel<CreateBudgetScreenViewModel>()
) {
    val categories by vm.categories.collectAsState()

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
                        onClick = {

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
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    label = {
                        Text(text = "Name")
                    },
                    onValueChange = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                var amountText by remember {
                    mutableStateOf("")
                }

                OutlinedTextField(
                    value = amountText,
                    label = {
                        Text(text = "Amount*")
                    },
                    prefix = {
                        Text(text = account.defaultCurrency.symbol)
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
                            //focusManager.moveFocus(FocusDirection.Right)
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Column {
                    Text(text = "Categories")
                    var selected by remember {
                        mutableStateOf(listOf<Category>())
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(categories.size) { index ->
                            FilterChip(
                                selected = selected.contains(categories[index]),
                                onClick = {
                                    if (selected.contains(categories[index])) {
                                        selected = selected.minus(categories[index])
                                    } else {
                                        selected = selected.plus(categories[index])
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