package com.unghostdude.budjet.ui.home

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.util.Currency
import android.icu.util.ULocale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.TimeRange
import com.unghostdude.budjet.model.TransactionType
import com.unghostdude.budjet.viewmodel.analytic.AnalyticViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnalyticScreen(
    vm: AnalyticViewModel = hiltViewModel()
) {
    var dialog by remember {
        mutableStateOf(AnalyticDialog.None)
    }
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm")
    val amountFormatter = NumberFormatter
        .withLocale(ULocale.getDefault())
        .notation(Notation.compactShort())

    val transactions by vm.transactions.collectAsState()
    val account by vm.activeAccount.collectAsState()
    val range by vm.range.collectAsState()
    val timeKey = ExtraStore.Key<List<LocalTime>>()


    LazyColumn(
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        item {
            ProvideVicoTheme(theme = rememberM3VicoTheme()) {
                val chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = rememberStartAxis(
                        valueFormatter = { y, _, _ ->
                            if (account != null) {
                                amountFormatter
                                    .unit(Currency.getInstance(account!!.currency.currencyCode))
                                    .format(y)
                                    .toString()
                            } else {
                                y.toString()
                            }
                        }
                    ),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { x, chartValues, _ ->
                            when (range) {
                                TimeRange.TODAY -> {
                                    timeFormatter.format(chartValues.model.extraStore[timeKey][x.toInt()])
                                }

                                else -> {
                                    ""
                                }
                            }
                        }
                    )
                )

                val producer = remember {
                    CartesianChartModelProducer()
                }

                if (transactions.isNotEmpty()) {
                    LaunchedEffect(Unit) {
                        producer.runTransaction {
                            when (range) {
                                TimeRange.TODAY -> {
                                    val group = transactions.groupBy {
                                        it.date.toLocalTime()
                                    }

                                    val expenses = group.mapValues { entry ->
                                        entry.value.filter { transaction ->
                                            transaction.type != TransactionType.Income
                                        }.sumOf {
                                            it.amount
                                        }
                                    }

                                    lineSeries {
                                        series(expenses.values)
                                    }

                                    extras { it[timeKey] = expenses.keys.toList() }
                                }

                                else -> {

                                }
                            }
                        }
                    }

                    Column {
                        FlowRow(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            FilterChip(
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_done),
                                        contentDescription = null
                                    )
                                },
                                selected = true,
                                onClick = {
                                    dialog = AnalyticDialog.TimeRange
                                },
                                label = {
                                    Text(
                                        text = when (range) {
                                            TimeRange.TODAY -> "Today"
                                            TimeRange.ALL_TIME -> "All Time"
                                            TimeRange.THIS_WEEK -> "This Week"
                                            TimeRange.THIS_YEAR -> "This Year"
                                            TimeRange.THIS_MONTH -> "This Month"
                                            TimeRange.YESTERDAY -> "Yesterday"
                                        }
                                    )
                                }
                            )
                        }
                        Card {
                            CartesianChartHost(
                                chart = chart,
                                modelProducer = producer,
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            //
        }
    }

    if (dialog == AnalyticDialog.TimeRange) {
        ModalBottomSheet(
            onDismissRequest = {
                dialog = AnalyticDialog.None
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Selected Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

enum class AnalyticDialog {
    None,
    TimeRange
}