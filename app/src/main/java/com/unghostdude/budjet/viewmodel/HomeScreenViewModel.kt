package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.model.AccountEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
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
class HomeScreenViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
    private val userRepo: AppSettingRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    val accounts = accountRepo.get().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )

    fun switchAccount(account: AccountEntity) {
        viewModelScope.launch {
            userRepo.setActiveAccount(account.id)
        }
    }

    fun exportToCsv(baseDirectory: String, callback: (path: String, file: File) -> Unit) {
        viewModelScope.launch {
            val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withZone(ZoneId.systemDefault())

            val dir = File(
                baseDirectory,
                "/exports/csv/"
            )

            if(!dir.exists()){
                dir.mkdirs()
            }

            val file = File(
                dir,
                "budjet_${
                    LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("Ld_Hm").withZone(ZoneId.systemDefault())
                    )
                }.csv"
            )

            val transactions = transactionRepository.get().firstOrNull()?.map { t ->
                "${dateFormatter.format(t.transaction.date)}, ${t.transaction.title}, ${t.transaction.amount}, ${t.transaction.type}, ${t.transaction.currency.currencyCode}, ${t.account.name}"
            } ?: listOf()

            file.createNewFile()
            val writer = FileWriter(file)
            writer.appendLine("date, title, amount, type, currency, account name")
            transactions.forEach {
                writer.appendLine(it)
            }
            writer.close()
            callback(file.absolutePath, file)
        }
    }
}