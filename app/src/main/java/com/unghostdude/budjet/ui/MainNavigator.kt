package com.unghostdude.budjet.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unghostdude.budjet.ui.budget.CreateBudgetScreen
import com.unghostdude.budjet.ui.home.HomeScreen
import com.unghostdude.budjet.ui.onboarding.AccountSetupScreen
import com.unghostdude.budjet.ui.onboarding.OnboardingScreen
import com.unghostdude.budjet.ui.setting.SettingScreen
import com.unghostdude.budjet.ui.transaction.CreateTransactionScreen
import com.unghostdude.budjet.ui.transaction.TransactionDetailScreen
import com.unghostdude.budjet.ui.transaction.TransactionScreen
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
        val preference = state as MainNavigatorState.Idle

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                if (preference.isFirstTime) {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                } else if (preference.account == null) {
                    navController.navigate(Screen.AccountSetup.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                } else {
                    HomeScreen(
                        account = preference.account,
                        username = preference.username,
                        navigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        },
                        navigateToTransactionDetail = { id ->
                            navController.navigate(Screen.Transaction.route + "/" + id)
                        },
                        navigateToNewTransaction = {
                            navController.navigate(Screen.NewTransaction.route)
                        },
                        navigateToNewBudget = {
                            navController.navigate(Screen.NewBudget.route)
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
                if (preference.account == null) {
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

            composable(Screen.NewTransaction.route) {
                if (preference.account != null) {
                    CreateTransactionScreen(
                        account = preference.account,
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

            composable(Screen.Settings.route) {
                SettingScreen(
                    navigateAway = {
                        navController.navigateUp()
                    }
                )
            }

            composable(
                Screen.TransactionDetail.route,
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

            composable(Screen.NewBudget.route) {
                if (preference.account != null) {
                    CreateBudgetScreen(
                        account = preference.account,
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
        }
    }
}