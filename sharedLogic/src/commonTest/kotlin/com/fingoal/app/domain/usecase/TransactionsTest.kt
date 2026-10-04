package com.fingoal.app.domain.usecase

import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.repository.TransactionRepository
import com.fingoal.app.domain.usecase.transactions.AddTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.DeleteTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.GetTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.SyncTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.UpdateTransactionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionsTest {

    private val transaction1 = Transaction(
        id = 1L,
        remoteId = "remote-1",
        title = "Sueldo",
        description = "Sueldo mensual",
        amount = 500000.0,
        category = "Trabajo",
        date = 1759276800000L,
        isIncome = true
    )

    private val transaction2 = Transaction(
        id = 2L,
        remoteId = "remote-2",
        title = "Supermercado",
        description = "Compras del mes",
        amount = 50000.0,
        category = "Comida",
        date = 1759363200000L,
        isIncome = false
    )

    // ---------------------------------------------------------
    // GET TRANSACTIONS
    // ---------------------------------------------------------

    @Test
    fun getTransactions_returnsRepositoryTransactions() = runTest {
        val fakeRepository = FakeTransactionRepository()
        fakeRepository.transactions.value = listOf(
            transaction1,
            transaction2
        )

        val useCase = GetTransactionsUseCase(fakeRepository)

        val result = useCase().first()

        assertEquals(2, result.size)
        assertEquals(transaction1, result[0])
        assertEquals(transaction2, result[1])
    }

    // ---------------------------------------------------------
    // ADD TRANSACTION
    // ---------------------------------------------------------

    @Test
    fun addTransaction_callsRepositoryWithCorrectData() = runTest {
        val fakeRepository = FakeTransactionRepository()
        val useCase = AddTransactionUseCase(fakeRepository)

        useCase(
            title = "Supermercado",
            amount = 50000.0,
            category = "Comida",
            isIncome = false,
            description = "Compras del mes"
        )

        assertTrue(fakeRepository.addTransactionCalled)

        assertEquals("Supermercado", fakeRepository.addedTitle)
        assertEquals(50000.0, fakeRepository.addedAmount)
        assertEquals("Comida", fakeRepository.addedCategory)
        assertEquals(false, fakeRepository.addedIsIncome)
        assertEquals("Compras del mes", fakeRepository.addedDescription)
    }

    // ---------------------------------------------------------
    // UPDATE TRANSACTION
    // ---------------------------------------------------------

    @Test
    fun updateTransaction_callsRepositoryWithCorrectData() = runTest {
        val fakeRepository = FakeTransactionRepository()
        val useCase = UpdateTransactionUseCase(fakeRepository)

        useCase(
            localId = 10L,
            remoteId = "remote-10",
            title = "Supermercado actualizado",
            amount = 60000.0,
            category = "Comida",
            isIncome = false,
            description = "Compra actualizada"
        )

        assertTrue(fakeRepository.updateTransactionCalled)

        assertEquals(10L, fakeRepository.updatedLocalId)
        assertEquals("remote-10", fakeRepository.updatedRemoteId)
        assertEquals("Supermercado actualizado", fakeRepository.updatedTitle)
        assertEquals(60000.0, fakeRepository.updatedAmount)
        assertEquals("Comida", fakeRepository.updatedCategory)
        assertEquals(false, fakeRepository.updatedIsIncome)
        assertEquals("Compra actualizada", fakeRepository.updatedDescription)
    }

    // ---------------------------------------------------------
    // DELETE TRANSACTION
    // ---------------------------------------------------------

    @Test
    fun deleteTransaction_callsRepositoryWithCorrectIds() = runTest {
        val fakeRepository = FakeTransactionRepository()
        val useCase = DeleteTransactionUseCase(fakeRepository)

        useCase(
            localId = 15L,
            remoteId = "remote-15"
        )

        assertTrue(fakeRepository.deleteTransactionCalled)

        assertEquals(15L, fakeRepository.deletedLocalId)
        assertEquals("remote-15", fakeRepository.deletedRemoteId)
    }

    // ---------------------------------------------------------
    // SYNC TRANSACTIONS
    // ---------------------------------------------------------

    @Test
    fun syncTransactions_callsRepository() = runTest {
        val fakeRepository = FakeTransactionRepository()
        val useCase = SyncTransactionsUseCase(fakeRepository)

        useCase()

        assertTrue(fakeRepository.syncTransactionsCalled)
    }
}

/**
 * Fake del TransactionRepository para probar los UseCases
 * sin depender de Room, API o implementaciones reales.
 */
private class FakeTransactionRepository : TransactionRepository {

    val transactions = MutableStateFlow<List<Transaction>>(emptyList())

    var addTransactionCalled = false
    var updateTransactionCalled = false
    var deleteTransactionCalled = false
    var syncTransactionsCalled = false

    var addedTitle = ""
    var addedAmount = 0.0
    var addedCategory = ""
    var addedIsIncome = false
    var addedDescription = ""

    var updatedLocalId = 0L
    var updatedRemoteId = ""
    var updatedTitle = ""
    var updatedAmount = 0.0
    var updatedCategory = ""
    var updatedIsIncome = false
    var updatedDescription = ""

    var deletedLocalId = 0L
    var deletedRemoteId = ""

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactions
    }

    override suspend fun syncTransactions() {
        syncTransactionsCalled = true
    }

    override suspend fun addTransaction(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {
        addTransactionCalled = true

        addedTitle = title
        addedAmount = amount
        addedCategory = category
        addedIsIncome = isIncome
        addedDescription = description
    }

    override suspend fun deleteTransaction(
        localId: Long,
        remoteId: String
    ) {
        deleteTransactionCalled = true

        deletedLocalId = localId
        deletedRemoteId = remoteId
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
        updateTransactionCalled = true

        updatedLocalId = localId
        updatedRemoteId = remoteId
        updatedTitle = title
        updatedAmount = amount
        updatedCategory = category
        updatedIsIncome = isIncome
        updatedDescription = description
    }
}