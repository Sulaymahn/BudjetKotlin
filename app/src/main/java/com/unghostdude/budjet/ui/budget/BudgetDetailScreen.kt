package com.unghostdude.budjet.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.viewmodel.BudgetDetailScreenViewModel
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BudgetDetailScreen(
    budgetId: String,
    navigateAway: () -> Unit,
    vm: BudgetDetailScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        vm.fetch(budgetId)
    }

    val budget by vm.budget.collectAsState()
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())

    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        .withZone(ZoneId.systemDefault())

    val currencyFormatter =
        NumberFormat.getCurrencyInstance(LocalContext.current.resources.configuration.locales[0])

    if (budget == null) {
        Text(text = "Not found")
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Budget")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigateAway() }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {

                    }
                )
            }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Column {
                        Text(
                            text = "Name",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = "${budget?.budget?.name}"
                        )
                    }

                    Column {
                        Text(
                            text = "Amount",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = "${budget?.budget?.amount}"
                        )
                    }

                    Column {
                        Text(
                            text = "Recurrence",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Light
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(budget?.categories?.size ?: 0) { i ->
                                Text(
                                    text = budget!!.categories[i].name
                                )
                            }
                        }

                    }

                    Column {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = "${budget?.budget?.created}"
                        )
                    }
                }
            }
        }
    }
}