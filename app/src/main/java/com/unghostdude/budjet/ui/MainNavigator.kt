package com.unghostdude.budjet.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unghostdude.budjet.ui.account.AccountCreationScreen
import com.unghostdude.budjet.ui.account.AccountScreen
import com.unghostdude.budjet.ui.budget.BudgetCreationScreen
import com.unghostdude.budjet.ui.budget.BudgetDetailScreen
import com.unghostdude.budjet.ui.home.HomeScreen
import com.unghostdude.budjet.ui.onboarding.AccountSetupScreen
import com.unghostdude.budjet.ui.onboarding.OnboardingScreen
import com.unghostdude.budjet.ui.setting.SettingScreen
import com.unghostdude.budjet.ui.transaction.CreateTransactionScreen
import com.unghostdude.budjet.ui.transaction.TransactionDetailScreen
import com.unghostdude.budjet.viewmodel.MainNavigatorState
import com.unghostdude.budjet.viewmodel.MainNavigatorViewModel

@Composable
fun MainNavigator(
    vm: MainNavigatorViewModel = hiltViewModel<MainNavigatorViewModel>()
) {
    val navController = rememberNavController()
    val state by vm.preference.collectAsState()

    if (state == MainNavigatorState.Loading) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val uiState = state as MainNavigatorState.Idle

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                if (uiState.isFirstTime) {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                } else if (uiState.account == null) {
                    navController.navigate(Screen.AccountSetup.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                } else {
                    HomeScreen(
                        account = uiState.account,
                        username = uiState.username,
                        navigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        },
                        navigateToTransactionDetailScreen = { id ->
                            navController.navigate(Screen.Transaction.route + "/" + id)
                        },
                        navigateToTransactionCreationScreen = {
                            navController.navigate(Screen.TransactionCreation.route)
                        },
                        navigateToBudgetCreationScreen = {
                            navController.navigate(Screen.BudgetCreation.route)
                        },
                        navigateToBudgetDetail = { id ->
                            navController.navigate(Screen.Budget.route + "/" + id)
                        },
                        navigateToAccountScreen = {
                            navController.navigate(Screen.Account.route)
                        }
                    )
                }
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen {
                    navController.navigate(Screen.AccountSetup.route)
                }
            }

            composable(Screen.AccountSetup.route) {
                if (uiState.account == null) {
                    AccountSetupScreen {
                        navController.navigate(Screen.Home.route)
                    }
                } else {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            }

            composable(Screen.TransactionCreation.route) {
                if (uiState.account != null) {
                    CreateTransactionScreen(
                        account = uiState.account,
                        navigateAway = {
                            navController.navigateUp()
                        }
                    )
                } else {
                    navController.navigate(Screen.AccountSetup.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            }

            composable(Screen.AccountCreation.route){
                if(uiState.account != null){
                    AccountCreationScreen(
                        account = uiState.account,
                        navigateAway = {
                            navController.navigateUp()
                        }
                    )
                }else{
                    navController.navigate(Screen.AccountSetup.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            }

            composable(Screen.Settings.route) {
                SettingScreen(
                    navigateAway = {
                        navController.navigateUp()
                    }
                )
            }

            composable(
                route = Screen.TransactionDetail.route,
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                })
            ) {
                val id = navController.currentBackStackEntry?.arguments?.getString("id")
                TransactionDetailScreen(
                    transactionId = id ?: "",
                    navigatorAway = { navController.navigateUp() }
                )
            }

            composable(
                route = Screen.BudgetCreation.route
            ) {
                if (uiState.account != null) {
                    BudgetCreationScreen(
                        account = uiState.account,
                        navigateAway = {
                            navController.navigateUp()
                        }
                    )
                } else {
                    navController.navigate(Screen.AccountSetup.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            }

            composable(
                route = Screen.BudgetDetail.route,
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                })
            ) {
                val id = navController.currentBackStackEntry?.arguments?.getString("id")
                BudgetDetailScreen(
                    budgetId = id ?: "",
                    navigateAway = { navController.navigateUp() }
                )
            }

            composable(route = Screen.Account.route) {
                AccountScreen(
                    navigateAway = { navController.navigateUp() },
                    navigateToAccountCreation = {
                        navController.navigate(Screen.AccountCreation.route)
                    }
                )
            }
        }
    }
}