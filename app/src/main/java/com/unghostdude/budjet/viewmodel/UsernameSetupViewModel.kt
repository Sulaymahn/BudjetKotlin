package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsernameSetupViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository
) : ViewModel() {

    val username = FormControl(
        validators = listOf(Validators.Required())
    )

    fun canSetUsername(): Boolean {
        return username.peepValidity()
    }

    fun setUsername(onComplete: () -> Unit) {
        viewModelScope.launch {
            appSettingRepository.setUsername(username.currentValue)
            onComplete()
        }
    }
}