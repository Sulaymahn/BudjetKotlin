package com.unghostdude.budjet.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unghostdude.budjet.ui.home.HomeScreen
import com.unghostdude.budjet.ui.onboarding.AccountSetupScreen
import com.unghostdude.budjet.ui.onboarding.OnboardingScreen
import com.unghostdude.budjet.ui.transaction.CreateTransactionScreen

@Composable
fun MainNavigator() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navigateToNewTransaction = {
                    navController.navigate(Screen.NewTransaction.route)
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                navController.navigate(Screen.AccountSetup.route)
            }
        }

        composable(Screen.AccountSetup.route) {
            AccountSetupScreen {
                navController.navigate(Screen.Home.route)
            }
        }

        composable(Screen.NewTransaction.route){
            CreateTransactionScreen(
                navigateAway = {
                    navController.navigateUp()
                }
            )
        }
    }
}