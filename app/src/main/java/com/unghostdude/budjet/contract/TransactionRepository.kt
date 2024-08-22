package com.unghostdude.budjet.contract

import com.unghostdude.budjet.model.TimeRange
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionForCreation
import com.unghostdude.budjet.model.TransactionForUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TransactionRepository {
    suspend fun insert(transaction: TransactionForCreation)
    suspend fun update(transaction: TransactionForUpdate)
    suspend fun delete(transactionId: UUID)
    fun get(id: UUID): Flow<Transaction?>
    fun get(): Flow<List<Transaction>>
    fun getByAccount(accountId: UUID): Flow<List<Transaction>>
    fun getByCategory(categoryIds: List<Int>): Flow<List<Transaction>>
    fun getByDateRange(range: TimeRange) : Flow<List<Transaction>>
}