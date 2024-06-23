package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.BudgetRepository
import com.unghostdude.budjet.model.BudgetWithAccountAndCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BudgetScreenViewModel @Inject constructor(
    private val budgetRepo: BudgetRepository,
    private val settings: AppSettingRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val budgets = settings.activeAccount.flatMapLatest { accountId ->
        if (accountId == null) {
            flow { listOf<BudgetWithAccountAndCategories>() }
        } else {
            budgetRepo.getWithAccountId(accountId.toString())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )
}