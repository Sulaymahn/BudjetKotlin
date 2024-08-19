package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.model.AppTheme
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class SettingScreenViewModel @Inject constructor(
    private val settingRepository: AppSettingRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    fun updateUsername(username: String, callback: () -> Unit) {
        viewModelScope.launch {
            settingRepository.setUsername(username)
            callback()
        }
    }

    val settings = settingRepository.username
        .combine(
            settingRepository.theme
        ) { username, theme ->
            Settings(
                username = username,
                theme = theme
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = Settings(
                username = "",
                theme = AppTheme.System
            )
        )

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingRepository.setTheme(theme)
        }
    }

    fun exportToCsv(baseDirectory: String, callback: (path: String, file: File) -> Unit) {
        viewModelScope.launch {
            try {
                val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withZone(ZoneId.systemDefault())

                val dir = File(
                    baseDirectory,
                    "/exports/csv/"
                )

                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val filename = "budjet_${
                    LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("Ld_Hm")
                    )
                }.csv"

                val file = File(dir, filename)

                val transactions = transactionRepository.get().firstOrNull()?.map { t ->
                    "${dateFormatter.format(t.transaction.date)}, ${t.account.name}, ${t.transaction.title}, ${t.transaction.amount}, ${t.transaction.type}, ${t.transaction.currency.currencyCode}"
                } ?: listOf()

                file.createNewFile()
                FileWriter(file).use { writer ->
                    writer.appendLine("date, account, title, amount, type, currency, ")
                    transactions.forEach {
                        writer.appendLine(it)
                    }
                }

                callback(file.absolutePath, file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class Settings(
    val username: String,
    val theme: AppTheme
)