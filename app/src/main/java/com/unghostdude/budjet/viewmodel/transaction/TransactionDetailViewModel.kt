package com.unghostdude.budjet.viewmodel.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.CategoryRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.model.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    private var fetching = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val transaction = fetching.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            transactionRepository.get(id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        null
    )

    val accounts = accountRepository.get()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val categories = categoryRepository.get()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun fetch(id: String) {
        if (fetching.value != id) {
            fetching.value = id
        }
    }

    fun update(transactionEntity: TransactionEntity, callback: () -> Unit){
        viewModelScope.launch {
            transactionRepository.update(transactionEntity)
            callback()
        }
    }
}