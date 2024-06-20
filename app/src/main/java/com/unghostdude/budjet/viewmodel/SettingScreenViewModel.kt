package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingScreenViewModel @Inject constructor(
    private val settingRepository: AppSettingRepository
) : ViewModel() {

    fun updateUsername(username: String, callback: () -> Unit){
        viewModelScope.launch {
            settingRepository.setUsername(username)
            callback()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val settings = settingRepository.username.flatMapLatest {
        flowOf(
            Settings(
                username = it
            )
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        Settings(
            username = ""
        )
    )
}

data class Settings(
    val username: String
)