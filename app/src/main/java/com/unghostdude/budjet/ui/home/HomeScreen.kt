package com.unghostdude.budjet.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unghostdude.budjet.R
import com.unghostdude.budjet.ui.Screen
import com.unghostdude.budjet.ui.transaction.TransactionScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToNewTransaction: () -> Unit,
) {
    val navController = rememberNavController()
    val navState = navController.currentBackStackEntryAsState()
    val currentScreen = when (navState.value?.destination?.route) {
        Screen.Budget.route -> Screen.Budget
        Screen.Transaction.route -> Screen.Transaction
        Screen.Analytic.route -> Screen.Analytic
        else -> Screen.Dashboard
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(text = currentScreen.title)
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.Dashboard,
                    onClick = {
                        navController.navigate(Screen.Dashboard.route)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_dashboard),
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "Dashboard")
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.Budget,
                    onClick = {
                        navController.navigate(Screen.Budget.route)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_monetization_on),
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = Screen.Budget.title)
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.Analytic,
                    onClick = { navController.navigate(Screen.Analytic.route) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_analytics),
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = Screen.Analytic.title)
                    }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.Transaction,
                    onClick = { navController.navigate(Screen.Transaction.route) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_article),
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = Screen.Transaction.title)
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .padding(it)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }

            composable(Screen.Analytic.route) {
                AnalyticScreen()
            }

            composable(Screen.Transaction.route) {
                TransactionScreen {
                    navigateToNewTransaction()
                }
            }

            composable(Screen.Budget.route) {
                BudgetScreen()
            }
        }
    }
}