package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.model.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BudjetThemeViewModel @Inject constructor(
    private val settingRepository: AppSettingRepository
) : ViewModel() {
    val currentTheme = settingRepository.theme
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            AppTheme.System
        )
}