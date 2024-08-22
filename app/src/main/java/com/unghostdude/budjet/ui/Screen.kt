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
    data object UsernameSetup : Screen("username", "Settings")

    data object Transactions : Screen("transactions", "Transactions")
    data object TransactionCreation : Screen("transaction/create", "Transaction")
    data object TransactionDetail : Screen("transactions/{id}", "Transactions")

    data object Budget : Screen("budgets", "Budgets")
    data object BudgetCreation : Screen("budgets/create", "Budget")
    data object BudgetDetail : Screen("budgets/{id}", "Budgets")

    data object Account : Screen("accounts", "Accounts")
    data object AccountSetup : Screen("account_setup", "Account Setup")
    data object AccountCreation : Screen("accounts/create", "")
    data object AccountDetail : Screen("accounts/{id}", "")
}