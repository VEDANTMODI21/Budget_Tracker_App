package com.example.data

import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val financeDao: FinanceDao) {
    val allTransactions: Flow<List<Transaction>> = financeDao.getAllTransactions()
    val allDebts: Flow<List<Debt>> = financeDao.getAllDebts()

    suspend fun insertTransaction(transaction: Transaction) {
        financeDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        financeDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        financeDao.deleteTransactionById(id)
    }

    suspend fun insertDebt(debt: Debt) {
        financeDao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: Debt) {
        financeDao.updateDebt(debt)
    }

    suspend fun deleteDebt(debt: Debt) {
        financeDao.deleteDebt(debt)
    }

    suspend fun deleteDebtById(id: Int) {
        financeDao.deleteDebtById(id)
    }
}
