package com.unghostdude.budjet.viewmodel.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BudgetDetailScreenViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    private var fetching = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val budget = fetching.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            budgetRepository.get(id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        null
    )

    fun fetch(id: String) {
        if (fetching.value != id) {
            fetching.value = id
        }
    }
}