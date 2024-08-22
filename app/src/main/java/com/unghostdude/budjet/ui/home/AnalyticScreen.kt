package com.unghostdude.budjet.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.unghostdude.budjet.model.TimeRange
import com.unghostdude.budjet.model.TransactionType
import com.unghostdude.budjet.viewmodel.analytic.AnalyticViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AnalyticScreen(
    vm: AnalyticViewModel = hiltViewModel()
) {
    val transactions by vm.transactions.collectAsState()
    val range by vm.range.collectAsState()

    val chart = rememberCartesianChart(
        rememberLineCartesianLayer(),
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis()
    )
    val producer = remember {
        CartesianChartModelProducer()
    }

    if (transactions.isNotEmpty()) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.Default) {
                producer.runTransaction {
                    lineSeries {
                        when (range) {
                            TimeRange.TODAY -> {
                                val group = transactions.groupBy {
                                    it.date.hour
                                }

                                val incomes = group.mapValues { values ->
                                    values.value.filter { it.type == TransactionType.Income }
                                        .sumOf { transaction ->
                                            transaction.amount
                                        }
                                }

                                val expenses = group.mapValues { values ->
                                    values.value.filter { it.type == TransactionType.Expense }
                                        .sumOf { transaction ->
                                            transaction.amount
                                        }
                                }

                                series(x = expenses.keys, y = expenses.values)
                            }

                            else -> series(y = transactions.map { it.amount })
                        }

                    }
                }
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        item {
            Card {
                CartesianChartHost(
                    chart = chart,
                    modelProducer = producer
                )
            }
        }

        item {
            Text(text = range.toString())
        }
    }
}