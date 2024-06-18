package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    private val userData: AppSettingRepository
) : ViewModel() {
    fun completeSetup(callback: () -> Unit) {
        viewModelScope.launch {
            userData.setFirstTime(false)
            callback()
        }
    }
}