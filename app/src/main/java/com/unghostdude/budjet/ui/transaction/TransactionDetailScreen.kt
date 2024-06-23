package com.unghostdude.budjet.ui.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.unghostdude.budjet.viewmodel.TransactionDetailScreenViewModel
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    navigatorAway: () -> Unit,
    vm: TransactionDetailScreenViewModel = hiltViewModel<TransactionDetailScreenViewModel>()
) {
    LaunchedEffect(Unit) {
        vm.fetch(transactionId)
    }

    val transaction by vm.transaction.collectAsState()
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())

    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        .withZone(ZoneId.systemDefault())

    val currencyFormatter =
        NumberFormat.getCurrencyInstance(LocalContext.current.resources.configuration.locales[0])

    if (transaction == null) {
        Text(text = "Not found")
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Transaction")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigatorAway() }) {
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
                            text = "Title",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = "${transaction?.transaction?.title}"
                        )
                    }

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Type",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                text = "Amount",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = transaction?.transaction?.type.toString()
                            )
                            Text(
                                text = currencyFormatter.format(transaction?.transaction?.amount)
                            )
                        }
                    }

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                text = "Time",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = transaction?.category?.name ?: ""
                            )
                            Text(
                                text = timeFormatter.format(transaction?.transaction?.date)
                            )
                        }
                    }

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Currency",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = transaction?.transaction?.currency?.displayName ?: ""
                            )
                        }
                    }

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Note",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = transaction?.transaction?.note ?: ""
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    Text(text = dateFormatter.format(transaction?.transaction?.date))
                }
            }
        }
    }
}