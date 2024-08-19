package com.unghostdude.budjet.viewmodel.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.AccountForUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private var fetching = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val account = fetching.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            accountRepository.getWithBalance(UUID.fromString(id))
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

    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            if (fetching.value != null) {
                val acc = account.value!!;

                accountRepository.delete(
                    AccountEntity(
                        id = acc.id,
                        startAmount = acc.startAmount,
                        name = acc.name,
                        created = acc.created,
                        currency = acc.currency
                    )
                )

                onComplete()
            }
        }
    }

    fun updateAccount(accountUpdate: AccountForUpdate, onComplete: () -> Unit){
        viewModelScope.launch {
            accountRepository.update(
                AccountEntity(
                    id = accountUpdate.id,
                    currency = accountUpdate.currency,
                    created = account.value!!.created,
                    name = accountUpdate.name,
                    startAmount = accountUpdate.balance - account.value!!.balance
                )
            )

            onComplete()
        }
    }
}