package com.unghostdude.budjet.implementation

import com.unghostdude.budjet.contract.TransactionRepository
import com.unghostdude.budjet.data.TransactionDao
import com.unghostdude.budjet.model.DetailedTransaction
import com.unghostdude.budjet.model.TimeRange
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionEntity
import com.unghostdude.budjet.model.TransactionForCreation
import com.unghostdude.budjet.model.TransactionForUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.UUID

class RoomTransactionRepository(private val dao: TransactionDao) :
    TransactionRepository {
    override suspend fun insert(transaction: TransactionForCreation) {
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
        dao.insert(
            TransactionEntity(
                id = UUID.randomUUID(),
                lastModified = now,
                date = transaction.date.toInstant(),
                destinationAccountId = transaction.destinationAccount?.id,
                accountId = transaction.account.id,
                amount = transaction.amount,
                categoryId = transaction.category.id,
                created = now,
                dueDate = transaction.dueDate?.toInstant(),
                title = transaction.title,
                note = transaction.note,
                type = transaction.type,
                currency = transaction.account.currency
            )
        )
    }

    override suspend fun update(transaction: TransactionForUpdate) {
        val entity = dao.get(transaction.id.toString()).first()
        return dao.update(
            entity.transaction.copy(
                title = transaction.title,
                note = transaction.note,
                amount = transaction.amount,
                categoryId = transaction.category.id,
                type = transaction.type,
                destinationAccountId = transaction.destinationAccount?.id,
                accountId = transaction.account.id,
                currency = transaction.account.currency,
                dueDate = transaction.dueDate?.toInstant(),
                date = transaction.date.toInstant(),
                lastModified = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant(),
            )
        )
    }

    override suspend fun delete(transactionId: UUID) {
        val entity = dao.get(transactionId.toString()).first()
        return dao.delete(entity.transaction)
    }

    override fun get(id: UUID): Flow<Transaction> {
        return dao.get(id.toString()).map { transaction ->
            toTransaction(transaction)
        }
    }

    override fun get(): Flow<List<Transaction>> {
        return dao.get().map { transactions ->
            toTransactions(transactions)
        }
    }

    override fun getByAccount(accountId: UUID): Flow<List<Transaction>> {
        return dao.getByAccount(accountId.toString()).map { transactions ->
            toTransactions(transactions)
        }
    }


    override fun getByCategory(categoryIds: List<Int>): Flow<List<Transaction>> {
        return dao.getByCategory(categoryIds).map { transactions ->
            toTransactions(transactions)
        }
    }

    override fun getByDateRange(range: TimeRange): Flow<List<Transaction>> {
        return dao.get().map { transactions ->
            toTransactions(transactions).filter { transaction ->
                val now = LocalDateTime.now().atZone(ZoneId.systemDefault())

                when (range) {
                    TimeRange.ALL_TIME -> true
                    TimeRange.TODAY -> transaction.date.toLocalDate().isEqual(now.toLocalDate())
                    TimeRange.YESTERDAY -> transaction.date.toLocalDate().isEqual(now.minusDays(1).toLocalDate())
                    TimeRange.THIS_WEEK -> {
                        val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        val endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                        transaction.date.isAfter(startOfWeek.minusSeconds(1)) && transaction.date.isBefore(endOfWeek.plusSeconds(1))
                    }
                    TimeRange.THIS_MONTH -> {
                        val startOfMonth = now.withDayOfMonth(1)
                        val endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                        transaction.date.isAfter(startOfMonth.minusSeconds(1)) && transaction.date.isBefore(endOfMonth.plusSeconds(1))
                    }
                    TimeRange.THIS_YEAR -> {
                        val startOfYear = now.withDayOfYear(1)
                        val endOfYear = now.with(TemporalAdjusters.lastDayOfYear())
                        transaction.date.isAfter(startOfYear.minusSeconds(1)) && transaction.date.isBefore(endOfYear.plusSeconds(1))
                    }
                }
            }
        }
    }

    private fun toTransaction(transaction: DetailedTransaction): Transaction {
        return Transaction(
            id = transaction.transaction.id,
            lastModified = transaction.transaction.lastModified.atZone(ZoneId.systemDefault()),
            destinationAccount = transaction.destinationAccount,
            title = transaction.transaction.title,
            note = transaction.transaction.note,
            amount = transaction.transaction.amount,
            account = transaction.account,
            date = transaction.transaction.date.atZone(ZoneId.systemDefault()),
            category = transaction.category,
            dueDate = transaction.transaction.dueDate?.atZone(ZoneId.systemDefault()),
            created = transaction.transaction.created.atZone(ZoneId.systemDefault()),
            type = transaction.transaction.type,
            currency = transaction.transaction.currency
        )
    }

    private fun toTransactions(transactions: List<DetailedTransaction>): List<Transaction> {
        return transactions.map { transaction ->
            toTransaction(transaction)
        }
    }
}