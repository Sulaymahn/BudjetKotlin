package com.unghostdude.budjet.viewmodel;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.BudgetRepository
import com.unghostdude.budjet.data.ViewRepository
import com.unghostdude.budjet.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CreateBudgetScreenViewModel @Inject constructor(
    private val viewRepo: ViewRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    val categories = viewRepo.getCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    var amount: Double? by mutableStateOf(null)
    var category by mutableStateOf<Category?>(null)
}
