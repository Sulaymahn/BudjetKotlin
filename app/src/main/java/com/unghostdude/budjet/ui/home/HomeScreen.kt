package com.unghostdude.budjet.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.ui.Screen
import com.unghostdude.budjet.ui.budget.BudgetScreen
import com.unghostdude.budjet.ui.transaction.TransactionScreen
import com.unghostdude.budjet.viewmodel.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    account: Account,
    username: String,
    vm: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>(),
    navigateToSettings: () -> Unit,
    navigateToTransactionDetail: (id: String) -> Unit,
    navigateToNewTransaction: () -> Unit,
    navigateToNewBudget: () -> Unit
) {
    val navController = rememberNavController()
    val navState = navController.currentBackStackEntryAsState()
    val accounts by vm.accounts.collectAsState()
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
                    var showMenu by remember {
                        mutableStateOf(false)
                    }
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = {
                                showMenu = false
                            }
                        ) {
                            if (currentScreen == Screen.Transaction || currentScreen == Screen.Dashboard) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = "Export as csv")
                                    },
                                    onClick = {
                                        showMenu = false
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = {
                                    Text(text = "Settings")
                                },
                                onClick = {
                                    showMenu = false
                                    navigateToSettings()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(text = "Switch account")
                                },
                                onClick = {
                                    showMenu = false
                                    showAccountModal = true
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentScreen != Screen.Analytic) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (currentScreen == Screen.Transaction || currentScreen == Screen.Dashboard) {
                            navigateToNewTransaction()
                        } else if (currentScreen == Screen.Budget) {
                            navigateToNewBudget()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Create")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.Dashboard,
                    onClick = {
                        if (currentScreen != Screen.Dashboard) {
                            navController.navigate(Screen.Dashboard.route)
                        }
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
                        if (currentScreen != Screen.Budget) {
                            navController.navigate(Screen.Budget.route)
                        }
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
                    onClick = {
                        if (currentScreen != Screen.Analytic) {
                            navController.navigate(Screen.Analytic.route)
                        }
                    },
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
                    onClick = {
                        if (currentScreen != Screen.Transaction) {
                            navController.navigate(Screen.Transaction.route)
                        }
                    },
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
                DashboardScreen(
                    account = account,
                    username = username
                )
            }

            composable(Screen.Analytic.route) {
                AnalyticScreen()
            }

            composable(Screen.Transaction.route) {
                TransactionScreen(
                    navigateToTransactionDetail = navigateToTransactionDetail
                )
            }

            composable(Screen.Budget.route) {
                BudgetScreen()
            }
        }

        if (showAccountModal) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAccountModal = false
                }
            ) {
                Text(
                    text = "Accounts",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                repeat(accounts.size) {
                    ListItem(
                        headlineContent = {
                            Text(text = accounts[it].name)
                        },
                        supportingContent = {
                            Text(text = accounts[it].defaultCurrency.displayName)
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
                Spacer(modifier = Modifier.height(160.dp))
            }
        }
    }
}