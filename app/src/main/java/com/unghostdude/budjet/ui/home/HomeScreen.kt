package com.unghostdude.budjet.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.ui.Screen
import com.unghostdude.budjet.ui.budget.BudgetScreen
import com.unghostdude.budjet.ui.transaction.TransactionScreen
import com.unghostdude.budjet.viewmodel.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    account: AccountEntity,
    username: String,
    navigateToBudgetDetail: (String) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToTransactionDetailScreen: (id: String) -> Unit,
    navigateToTransactionCreationScreen: () -> Unit,
    navigateToBudgetCreationScreen: () -> Unit,
    navigateToAccountScreen: () -> Unit,
    vm: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>(),
) {
    val navController = rememberNavController()
    val navState = navController.currentBackStackEntryAsState()
    val accounts by vm.accounts.collectAsState()
    val context = LocalContext.current
    val currentScreen = when (navState.value?.destination?.route) {
        Screen.Budget.route -> Screen.Budget
        Screen.Transaction.route -> Screen.Transaction
        Screen.Analytic.route -> Screen.Analytic
        else -> Screen.Dashboard
    }

    var showAccountModal by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (currentScreen == Screen.Dashboard) {
                            Icon(
                                painter = painterResource(R.drawable.budjetlogo),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                        Text(text = currentScreen.title)
                    }
                },
                actions = {
                    IconButton(onClick = navigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentScreen != Screen.Analytic) {
                FloatingActionButton(
                    onClick = {
                        if (currentScreen == Screen.Transaction || currentScreen == Screen.Dashboard) {
                            navigateToTransactionCreationScreen()
                        } else if (currentScreen == Screen.Budget) {
                            navigateToBudgetCreationScreen()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(

            ) {
                val barItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.Dashboard,
                    onClick = {
                        if (currentScreen != Screen.Dashboard) {
                            navController.navigate(Screen.Dashboard.route)
                        }
                    },
                    icon = {
                        if (currentScreen == Screen.Dashboard) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_dashboard),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.outline_dashboard),
                                contentDescription = null
                            )
                        }
                    },
                    label = {
                        Text(text = "Dashboard")
                    },
                    colors = barItemColors
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.Budget,
                    onClick = {
                        if (currentScreen != Screen.Budget) {
                            navController.navigate(Screen.Budget.route)
                        }
                    },
                    icon = {
                        if (currentScreen == Screen.Budget) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_monetization_on),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.outline_monetization_on),
                                contentDescription = null
                            )
                        }
                    },
                    label = {
                        Text(text = Screen.Budget.title)
                    },
                    colors = barItemColors
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.Analytic,
                    onClick = {
                        if (currentScreen != Screen.Analytic) {
                            navController.navigate(Screen.Analytic.route)
                        }
                    },
                    icon = {
                        if (currentScreen == Screen.Analytic) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_analytics),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.outline_analytics),
                                contentDescription = null
                            )
                        }
                    },
                    label = {
                        Text(text = Screen.Analytic.title)
                    },
                    colors = barItemColors
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.Transaction,
                    onClick = {
                        if (currentScreen != Screen.Transaction) {
                            navController.navigate(Screen.Transaction.route)
                        }
                    },
                    icon = {
                        if (currentScreen == Screen.Transaction) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_article),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.outline_article),
                                contentDescription = null
                            )
                        }
                    },
                    label = {
                        Text(text = Screen.Transaction.title)
                    },
                    colors = barItemColors
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
                DashboardScreen(
                    username = username,
                    navigateToAccountScreen = navigateToAccountScreen,
                    navigateToSwitchSelectedAccount = {
                        showAccountModal = true
                    }
                )
            }

            composable(Screen.Analytic.route) {
                AnalyticScreen()
            }

            composable(Screen.Transaction.route) {
                TransactionScreen(
                    navigateToTransactionDetail = navigateToTransactionDetailScreen
                )
            }

            composable(Screen.Budget.route) {
                BudgetScreen(
                    navigateToBudgetDetail = navigateToBudgetDetail
                )
            }
        }

        if (showAccountModal) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAccountModal = false
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

                repeat(accounts.size) {
                    ListItem(
                        headlineContent = {
                            Text(text = accounts[it].name)
                        },
                        supportingContent = {
                            Text(text = accounts[it].currency.displayName)
                        },
                        trailingContent = {
                            if (accounts[it].id == account.id) {
                                Text(text = "active")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (account.id != accounts[it].id) {
                                    vm.switchAccount(accounts[it])
                                }

                                showAccountModal = false
                            }
                    )
                }


                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}