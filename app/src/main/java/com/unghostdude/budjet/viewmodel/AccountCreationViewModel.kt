package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import com.unghostdude.budjet.data.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountCreationViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

}