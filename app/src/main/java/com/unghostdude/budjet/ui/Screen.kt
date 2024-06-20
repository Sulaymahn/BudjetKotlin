package com.unghostdude.budjet.ui

sealed class Screen(
    val route: String,
    val title: String
) {
    data object Onboarding : Screen("onboard", "Onboarding")
    data object Home : Screen("home", "Home")
    data object Dashboard : Screen("dashboard", "Budjet")
    data object Analytic : Screen("analytics", "Analytics")
    data object Settings : Screen("settings", "Settings")
    data object Transaction : Screen("transaction", "Transactions")
    data object TransactionDetail : Screen("transaction/{id}", "Transactions")
    data object NewTransaction : Screen("new_transaction", "Transaction")
    data object NewBudget : Screen("new_budget", "Budget")
    data object Template : Screen("template", "Templates")
    data object Budget : Screen("budget", "Budgets")
    data object AccountSetup : Screen("account_setup", "Account Setup")
}