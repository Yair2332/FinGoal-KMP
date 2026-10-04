package com.fingoal.app.ui.screens

import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.repository.TransactionRepository
import com.fingoal.app.domain.usecase.transactions.AddTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.DeleteTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.GetTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.SyncTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.UpdateTransactionUseCase
import com.fingoal.app.ui.screens.transactions.TransactionViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertNull
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn

class TransactionViewModelTest {

    @Test
    fun initialState_loadsTransactions() = runTest {
        val repository = FakeTransactionRepository()

        repository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                title = "Sueldo",
                amount = 100000.0,
                category = "Trabajo",
                isIncome = true
            )
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.transactions.size)
        assertEquals(
            "Sueldo",
            viewModel.uiState.value.transactions.first().title
        )
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun showAddSheet_opensSheetAndClearsEditingTransaction() {
        val viewModel = createViewModel()

        viewModel.showAddSheet()

        assertTrue(viewModel.uiState.value.showBottomSheet)
        assertNull(viewModel.uiState.value.editingTransaction)
    }

    @Test
    fun showEditSheet_setsTransactionToEdit() {
        val transaction = createTransaction(
            id = 1L,
            title = "Compra"
        )

        val viewModel = createViewModel()

        viewModel.showEditSheet(transaction)

        assertTrue(viewModel.uiState.value.showBottomSheet)
        assertEquals(
            transaction,
            viewModel.uiState.value.editingTransaction
        )
    }

    @Test
    fun hideSheet_closesSheetAndClearsEditingTransaction() {
        val transaction = createTransaction(
            id = 1L,
            title = "Compra"
        )

        val viewModel = createViewModel()

        viewModel.showEditSheet(transaction)
        viewModel.hideSheet()

        assertFalse(viewModel.uiState.value.showBottomSheet)
        assertNull(viewModel.uiState.value.editingTransaction)
    }

    @Test
    fun showDeleteDialog_setsTransactionToDelete() {
        val transaction = createTransaction(
            id = 1L,
            title = "Compra"
        )

        val viewModel = createViewModel()

        viewModel.showDeleteDialog(transaction)

        assertEquals(
            transaction,
            viewModel.uiState.value.transactionToDelete
        )
    }

    @Test
    fun hideDeleteDialog_clearsTransactionToDelete() {
        val transaction = createTransaction(
            id = 1L,
            title = "Compra"
        )

        val viewModel = createViewModel()

        viewModel.showDeleteDialog(transaction)
        viewModel.hideDeleteDialog()

        assertNull(
            viewModel.uiState.value.transactionToDelete
        )
    }

    @Test
    fun insertTransaction_callsUseCaseWithCorrectData() = runTest {
        val repository = FakeTransactionRepository()
        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.insertTransaction(
            title = "Sueldo",
            amount = 250000.0,
            category = "Trabajo",
            isIncome = true,
            description = "Sueldo mensual"
        )

        advanceUntilIdle()

        assertEquals(1, repository.addCalls)

        assertEquals("Sueldo", repository.lastAddedTitle)
        assertEquals(250000.0, repository.lastAddedAmount)
        assertEquals("Trabajo", repository.lastAddedCategory)
        assertTrue(repository.lastAddedIsIncome)
        assertEquals(
            "Sueldo mensual",
            repository.lastAddedDescription
        )

        assertFalse(viewModel.uiState.value.showBottomSheet)
    }

    @Test
    fun insertTransaction_withError_setsErrorMessage() = runTest {
        val repository = FakeTransactionRepository()
        repository.addError = Exception("No se pudo guardar")

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.insertTransaction(
            title = "Compra",
            amount = 5000.0,
            category = "Compras",
            isIncome = false,
            description = "Compra"
        )

        advanceUntilIdle()

        assertEquals(
            "No se pudo guardar",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun deleteTransaction_callsUseCaseWithCorrectIds() = runTest {
        val repository = FakeTransactionRepository()

        val transaction = createTransaction(
            id = 15L,
            remoteId = "remote-15",
            title = "Compra"
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.showDeleteDialog(transaction)
        viewModel.deleteTransaction()

        advanceUntilIdle()

        assertEquals(1, repository.deleteCalls)
        assertEquals(15L, repository.lastDeletedLocalId)
        assertEquals(
            "remote-15",
            repository.lastDeletedRemoteId
        )

        assertNull(
            viewModel.uiState.value.transactionToDelete
        )
    }

    @Test
    fun deleteTransaction_withoutSelectedTransaction_doesNothing() = runTest {
        val repository = FakeTransactionRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.deleteTransaction()

        advanceUntilIdle()

        assertEquals(0, repository.deleteCalls)
    }

    @Test
    fun deleteTransaction_withError_setsErrorMessage() = runTest {
        val repository = FakeTransactionRepository()
        repository.deleteError =
            Exception("No se pudo eliminar")

        val transaction = createTransaction(
            id = 15L,
            remoteId = "remote-15"
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.showDeleteDialog(transaction)
        viewModel.deleteTransaction()

        advanceUntilIdle()

        assertEquals(
            "Error al borrar: No se pudo eliminar",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun editTransaction_callsUseCaseWithCorrectData() = runTest {
        val repository = FakeTransactionRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.editTransaction(
            localId = 20L,
            remoteId = "remote-20",
            title = "Alquiler",
            amount = 150000.0,
            category = "Hogar",
            isIncome = false,
            description = "Alquiler mensual"
        )

        advanceUntilIdle()

        assertEquals(1, repository.updateCalls)

        assertEquals(20L, repository.lastUpdatedLocalId)
        assertEquals(
            "remote-20",
            repository.lastUpdatedRemoteId
        )
        assertEquals(
            "Alquiler",
            repository.lastUpdatedTitle
        )
        assertEquals(
            150000.0,
            repository.lastUpdatedAmount
        )
        assertEquals(
            "Hogar",
            repository.lastUpdatedCategory
        )
        assertFalse(repository.lastUpdatedIsIncome)
        assertEquals(
            "Alquiler mensual",
            repository.lastUpdatedDescription
        )

        assertFalse(
            viewModel.uiState.value.showBottomSheet
        )
    }

    @Test
    fun editTransaction_withError_setsErrorMessage() = runTest {
        val repository = FakeTransactionRepository()

        repository.updateError =
            Exception("No se pudo editar")

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.editTransaction(
            localId = 20L,
            remoteId = "remote-20",
            title = "Alquiler",
            amount = 150000.0,
            category = "Hogar",
            isIncome = false,
            description = "Alquiler"
        )

        advanceUntilIdle()

        assertEquals(
            "Error al editar: No se pudo editar",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun assistantQuestions_calculatesTotalsAndCounts() = runTest {
        val repository = FakeTransactionRepository()

        repository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                title = "Sueldo",
                amount = 100000.0,
                isIncome = true
            ),
            createTransaction(
                id = 2L,
                title = "Freelance",
                amount = 50000.0,
                isIncome = true
            ),
            createTransaction(
                id = 3L,
                title = "Comida",
                amount = 20000.0,
                isIncome = false
            ),
            createTransaction(
                id = 4L,
                title = "Transporte",
                amount = 10000.0,
                isIncome = false
            )
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val questions =
            viewModel.uiState.value.assistantQuestions

        assertEquals(5, questions.size)

        assertEquals(
            "En total ingresaste $150000.0.",
            questions[0].answer
        )

        assertEquals(
            "En total gastaste $30000.0.",
            questions[1].answer
        )

        assertEquals(
            "Tienes 4 transacciones registradas.",
            questions[2].answer
        )

        assertEquals(
            "Tienes 2 ingresos registrados.",
            questions[3].answer
        )

        assertEquals(
            "Tienes 2 gastos registrados.",
            questions[4].answer
        )
    }

    @Test
    fun assistantQuestions_withoutTransactions_showsZeros() = runTest {
        val repository = FakeTransactionRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val questions =
            viewModel.uiState.value.assistantQuestions

        assertEquals(5, questions.size)

        assertEquals(
            "En total ingresaste $0.0.",
            questions[0].answer
        )

        assertEquals(
            "En total gastaste $0.0.",
            questions[1].answer
        )

        assertEquals(
            "Tienes 0 transacciones registradas.",
            questions[2].answer
        )

        assertEquals(
            "Tienes 0 ingresos registrados.",
            questions[3].answer
        )

        assertEquals(
            "Tienes 0 gastos registrados.",
            questions[4].answer
        )
    }

    @Test
    fun getMonthlyIncome_returnsOnlyCurrentMonthIncome() = runTest {
        val repository = FakeTransactionRepository()

        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val currentMonthDate = LocalDate(
            year = now.year,
            monthNumber = now.monthNumber,
            dayOfMonth = 5
        ).atStartOfDayIn(
            TimeZone.currentSystemDefault()
        ).toEpochMilliseconds()

        val previousMonthDate = LocalDate(
            year = if (now.monthNumber == 1) {
                now.year - 1
            } else {
                now.year
            },
            monthNumber = if (now.monthNumber == 1) {
                12
            } else {
                now.monthNumber - 1
            },
            dayOfMonth = 20
        ).atStartOfDayIn(
            TimeZone.currentSystemDefault()
        ).toEpochMilliseconds()

        repository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                amount = 100000.0,
                isIncome = true,
                date = currentMonthDate
            ),
            createTransaction(
                id = 2L,
                amount = 50000.0,
                isIncome = true,
                date = previousMonthDate
            )
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertEquals(
            100000.0,
            viewModel.getMonthlyIncome()
        )
    }

    @Test
    fun getMonthlyExpenses_returnsOnlyCurrentMonthExpenses() = runTest {
        val repository = FakeTransactionRepository()

        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val currentMonthDate = LocalDate(
            year = now.year,
            monthNumber = now.monthNumber,
            dayOfMonth = 10
        ).atStartOfDayIn(
            TimeZone.currentSystemDefault()
        ).toEpochMilliseconds()

        val previousMonthDate = LocalDate(
            year = if (now.monthNumber == 1) {
                now.year - 1
            } else {
                now.year
            },
            monthNumber = if (now.monthNumber == 1) {
                12
            } else {
                now.monthNumber - 1
            },
            dayOfMonth = 20
        ).atStartOfDayIn(
            TimeZone.currentSystemDefault()
        ).toEpochMilliseconds()

        repository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                amount = 30000.0,
                isIncome = false,
                date = currentMonthDate
            ),
            createTransaction(
                id = 2L,
                amount = 70000.0,
                isIncome = false,
                date = previousMonthDate
            )
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertEquals(
            30000.0,
            viewModel.getMonthlyExpenses()
        )
    }

    @Test
    fun getExpenseCategorySummary_groupsCurrentMonthExpensesAndCalculatesPercentages() =
        runTest {
            val repository = FakeTransactionRepository()

            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())

            val currentMonthDate = LocalDate(
                year = now.year,
                monthNumber = now.monthNumber,
                dayOfMonth = 10
            ).atStartOfDayIn(
                TimeZone.currentSystemDefault()
            ).toEpochMilliseconds()

            val previousMonthDate = LocalDate(
                year = if (now.monthNumber == 1) {
                    now.year - 1
                } else {
                    now.year
                },
                monthNumber = if (now.monthNumber == 1) {
                    12
                } else {
                    now.monthNumber - 1
                },
                dayOfMonth = 20
            ).atStartOfDayIn(
                TimeZone.currentSystemDefault()
            ).toEpochMilliseconds()

            repository.transactions.value = listOf(
                createTransaction(
                    id = 1L,
                    category = "Alimentos",
                    amount = 50000.0,
                    isIncome = false,
                    date = currentMonthDate
                ),
                createTransaction(
                    id = 2L,
                    category = "Alimentos",
                    amount = 25000.0,
                    isIncome = false,
                    date = currentMonthDate
                ),
                createTransaction(
                    id = 3L,
                    category = "Transporte",
                    amount = 25000.0,
                    isIncome = false,
                    date = currentMonthDate
                ),
                createTransaction(
                    id = 4L,
                    category = "Hogar",
                    amount = 100000.0,
                    isIncome = false,
                    date = previousMonthDate
                ),
                createTransaction(
                    id = 5L,
                    category = "Trabajo",
                    amount = 50000.0,
                    isIncome = true,
                    date = currentMonthDate
                )
            )

            val viewModel = createViewModel(repository)

            advanceUntilIdle()

            val summary =
                viewModel.getExpenseCategorySummary()

            assertEquals(2, summary.size)

            assertEquals(
                "Alimentos",
                summary[0].category
            )

            assertEquals(
                75000.0,
                summary[0].amount
            )

            assertEquals(
                0.75f,
                summary[0].percentage
            )

            assertEquals(
                "Transporte",
                summary[1].category
            )

            assertEquals(
                25000.0,
                summary[1].amount
            )

            assertEquals(
                0.25f,
                summary[1].percentage
            )
        }

    @Test
    fun getExpenseCategorySummary_withoutExpenses_returnsEmptyList() = runTest {
        val repository = FakeTransactionRepository()

        repository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                amount = 100000.0,
                isIncome = true
            )
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertTrue(
            viewModel.getExpenseCategorySummary().isEmpty()
        )
    }

    private fun createViewModel(
        repository: FakeTransactionRepository = FakeTransactionRepository()
    ): TransactionViewModel {
        return TransactionViewModel(
            getTransactionsUseCase = GetTransactionsUseCase(repository),
            addTransactionUseCase = AddTransactionUseCase(repository),
            syncTransactionsUseCase = SyncTransactionsUseCase(repository),
            deleteTransactionUseCase = DeleteTransactionUseCase(repository),
            updateTransactionUseCase = UpdateTransactionUseCase(repository)
        )
    }

    private fun createTransaction(
        id: Long,
        remoteId: String = "remote-$id",
        title: String = "Transacción",
        description: String = "Descripción",
        amount: Double = 1000.0,
        category: String = "Otros",
        date: Long = Clock.System.now().toEpochMilliseconds(),
        isIncome: Boolean = false
    ): Transaction {
        return Transaction(
            id = id,
            remoteId = remoteId,
            title = title,
            description = description,
            amount = amount,
            category = category,
            date = date,
            isIncome = isIncome
        )
    }
}

private class FakeTransactionRepository : TransactionRepository {

    val transactions =
        MutableStateFlow<List<Transaction>>(emptyList())

    var syncCalls = 0
    var addCalls = 0
    var deleteCalls = 0
    var updateCalls = 0

    var lastAddedTitle: String? = null
    var lastAddedAmount: Double? = null
    var lastAddedCategory: String? = null
    var lastAddedIsIncome = false
    var lastAddedDescription: String? = null

    var lastDeletedLocalId: Long? = null
    var lastDeletedRemoteId: String? = null

    var lastUpdatedLocalId: Long? = null
    var lastUpdatedRemoteId: String? = null
    var lastUpdatedTitle: String? = null
    var lastUpdatedAmount: Double? = null
    var lastUpdatedCategory: String? = null
    var lastUpdatedIsIncome = false
    var lastUpdatedDescription: String? = null

    var addError: Exception? = null
    var deleteError: Exception? = null
    var updateError: Exception? = null

    override fun getTransactions(): Flow<List<Transaction>> =
        transactions

    override suspend fun syncTransactions() {
        syncCalls++
    }

    override suspend fun addTransaction(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {
        addCalls++

        lastAddedTitle = title
        lastAddedAmount = amount
        lastAddedCategory = category
        lastAddedIsIncome = isIncome
        lastAddedDescription = description

        addError?.let {
            throw it
        }
    }

    override suspend fun deleteTransaction(
        localId: Long,
        remoteId: String
    ) {
        deleteCalls++

        lastDeletedLocalId = localId
        lastDeletedRemoteId = remoteId

        deleteError?.let {
            throw it
        }
    }

    override suspend fun updateTransaction(
        localId: Long,
        remoteId: String,
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {
        updateCalls++

        lastUpdatedLocalId = localId
        lastUpdatedRemoteId = remoteId
        lastUpdatedTitle = title
        lastUpdatedAmount = amount
        lastUpdatedCategory = category
        lastUpdatedIsIncome = isIncome
        lastUpdatedDescription = description

        updateError?.let {
            throw it
        }
    }
}