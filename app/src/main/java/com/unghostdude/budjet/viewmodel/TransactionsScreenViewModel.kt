package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import com.unghostdude.budjet.data.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor(repo: TransactionRepository) : ViewModel() {
    val transactions = repo.get()
}