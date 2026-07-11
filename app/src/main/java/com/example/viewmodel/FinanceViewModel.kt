package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Debt
import com.example.data.FinanceRepository
import com.example.data.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    // All Transactions State Flow
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All Debts State Flow
    val debts: StateFlow<List<Debt>> = repository.allDebts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Derived Statistics
    val financeStats: StateFlow<FinanceStats> = transactions.map { txnList ->
        calculateStats(txnList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinanceStats()
    )

    // Derived Debt Summaries
    val debtStats: StateFlow<DebtStats> = debts.map { debtList ->
        calculateDebtStats(debtList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DebtStats()
    )

    // Expense Categories Breakdown (For Current Month Charts)
    val monthlyCategoryBreakdown: StateFlow<Map<String, Double>> = transactions.map { txnList ->
        val currentMonthTxns = txnList.filter {
            it.type == "EXPENSE" && isCurrentMonth(it.timestamp)
        }
        currentMonthTxns.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    // Actions
    fun addTransaction(amount: Double, category: String, description: String, timestamp: Long, type: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    amount = amount,
                    category = category,
                    description = description,
                    timestamp = timestamp,
                    type = type
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun addDebt(personName: String, amount: Double, type: String, description: String, dueDate: Long) {
        viewModelScope.launch {
            repository.insertDebt(
                Debt(
                    personName = personName,
                    amount = amount,
                    type = type,
                    description = description,
                    timestamp = System.currentTimeMillis(),
                    dueDate = dueDate
                )
            )
        }
    }

    fun resolveDebt(debt: Debt, autoLogTransaction: Boolean) {
        viewModelScope.launch {
            // Mark as resolved
            val resolvedDebt = debt.copy(isResolved = true)
            repository.updateDebt(resolvedDebt)

            if (autoLogTransaction) {
                // Log automatically to payments
                val txnType = if (debt.type == "LENT") "INCOME" else "EXPENSE"
                val category = if (debt.type == "LENT") "Lending Repaid" else "Debt Paid"
                val description = if (debt.type == "LENT") {
                    "Received lent money back from ${debt.personName}: ${debt.description}"
                } else {
                    "Repaid borrowed money to ${debt.personName}: ${debt.description}"
                }
                repository.insertTransaction(
                    Transaction(
                        amount = debt.amount,
                        category = category,
                        description = description,
                        timestamp = System.currentTimeMillis(),
                        type = txnType
                    )
                )
            }
        }
    }

    fun deleteDebt(debt: Debt) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // Helper functions to check dates
    private fun calculateStats(txns: List<Transaction>): FinanceStats {
        val expenses = txns.filter { it.type == "EXPENSE" }
        val income = txns.filter { it.type == "INCOME" }

        val todaySpent = expenses.filter { isToday(it.timestamp) }.sumOf { it.amount }
        val weekSpent = expenses.filter { isCurrentWeek(it.timestamp) }.sumOf { it.amount }
        val monthSpent = expenses.filter { isCurrentMonth(it.timestamp) }.sumOf { it.amount }
        val yearSpent = expenses.filter { isCurrentYear(it.timestamp) }.sumOf { it.amount }

        val totalIncome = income.sumOf { it.amount }
        val totalExpense = expenses.sumOf { it.amount }

        return FinanceStats(
            todaySpent = todaySpent,
            weekSpent = weekSpent,
            monthSpent = monthSpent,
            yearSpent = yearSpent,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = totalIncome - totalExpense
        )
    }

    private fun calculateDebtStats(debtsList: List<Debt>): DebtStats {
        val activeDebts = debtsList.filter { !it.isResolved }
        val totalLent = activeDebts.filter { it.type == "LENT" }.sumOf { it.amount }
        val totalBorrowed = activeDebts.filter { it.type == "BORROWED" }.sumOf { it.amount }

        return DebtStats(
            totalLent = totalLent,
            totalBorrowed = totalBorrowed,
            activeDebts = activeDebts
        )
    }

    private fun isToday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isCurrentWeek(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    }

    private fun isCurrentMonth(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }

    private fun isCurrentYear(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }
}

data class FinanceStats(
    val todaySpent: Double = 0.0,
    val weekSpent: Double = 0.0,
    val monthSpent: Double = 0.0,
    val yearSpent: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

data class DebtStats(
    val totalLent: Double = 0.0, // Money people owe me (given to people)
    val totalBorrowed: Double = 0.0, // Money I owe people
    val activeDebts: List<Debt> = emptyList()
)

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
