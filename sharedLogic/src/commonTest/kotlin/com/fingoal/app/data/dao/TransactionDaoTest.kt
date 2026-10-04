package com.fingoal.app.data.local.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.fingoal.app.data.local.database.AppDatabase
import com.fingoal.app.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TransactionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: TransactionDao

    @BeforeTest
    fun setup() {
        database = Room.inMemoryDatabaseBuilder<AppDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()

        dao = database.transactionDao()
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTransaction_savesTransaction() = runTest {
        val transaction = TransactionEntity(
            title = "Supermercado",
            description = "Compra semanal",
            amount = 25000.0,
            category = "Alimentos",
            date = 1000L,
            isIncome = false
        )

        dao.insertTransaction(transaction)

        val result = dao.getAllTransactions().first()

        assertEquals(1, result.size)
        assertEquals("Supermercado", result.first().title)
        assertEquals(25000.0, result.first().amount)
        assertEquals("Alimentos", result.first().category)
        assertTrue(!result.first().isIncome)
    }

    @Test
    fun insertTransactions_savesMultipleTransactions() = runTest {
        val transactions = listOf(
            TransactionEntity(
                title = "Sueldo",
                amount = 100000.0,
                category = "Trabajo",
                date = 1000L,
                isIncome = true
            ),
            TransactionEntity(
                title = "Comida",
                amount = 15000.0,
                category = "Alimentos",
                date = 2000L,
                isIncome = false
            )
        )

        dao.insertTransactions(transactions)

        val result = dao.getAllTransactions().first()

        assertEquals(2, result.size)
    }

    @Test
    fun getAllTransactions_returnsOrderedByDateDescending() = runTest {
        dao.insertTransactions(
            listOf(
                TransactionEntity(
                    title = "Primera",
                    amount = 100.0,
                    category = "Otros",
                    date = 1000L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Segunda",
                    amount = 200.0,
                    category = "Otros",
                    date = 3000L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Tercera",
                    amount = 300.0,
                    category = "Otros",
                    date = 2000L,
                    isIncome = false
                )
            )
        )

        val result = dao.getAllTransactions().first()

        assertEquals("Segunda", result[0].title)
        assertEquals("Tercera", result[1].title)
        assertEquals("Primera", result[2].title)
    }

    @Test
    fun getTransactionById_returnsCorrectTransaction() = runTest {
        dao.insertTransaction(
            TransactionEntity(
                title = "Internet",
                amount = 12000.0,
                category = "Servicios",
                date = 1000L,
                isIncome = false
            )
        )

        val saved = dao.getAllTransactions().first().first()

        val result = dao.getTransactionById(saved.id)

        assertEquals("Internet", result?.title)
        assertEquals(12000.0, result?.amount)
    }

    @Test
    fun getTransactionById_returnsNullWhenNotFound() = runTest {
        val result = dao.getTransactionById(999L)

        assertNull(result)
    }

    @Test
    fun deleteTransaction_removesTransaction() = runTest {
        dao.insertTransaction(
            TransactionEntity(
                title = "Eliminar",
                amount = 5000.0,
                category = "Otros",
                date = 1000L,
                isIncome = false
            )
        )

        val saved = dao.getAllTransactions().first().first()

        dao.deleteTransaction(saved)

        val result = dao.getAllTransactions().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun deleteById_removesTransaction() = runTest {
        dao.insertTransaction(
            TransactionEntity(
                title = "Eliminar",
                amount = 5000.0,
                category = "Otros",
                date = 1000L,
                isIncome = false
            )
        )

        val saved = dao.getAllTransactions().first().first()

        dao.deleteById(saved.id)

        assertNull(dao.getTransactionById(saved.id))
    }

    @Test
    fun clearAllTransactions_removesAllTransactions() = runTest {
        dao.insertTransactions(
            listOf(
                TransactionEntity(
                    title = "Uno",
                    amount = 100.0,
                    category = "Otros",
                    date = 1000L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Dos",
                    amount = 200.0,
                    category = "Otros",
                    date = 2000L,
                    isIncome = false
                )
            )
        )

        dao.clearAllTransactions()

        val result = dao.getAllTransactions().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun replaceAllTransactions_replacesPreviousData() = runTest {
        dao.insertTransaction(
            TransactionEntity(
                title = "Anterior",
                amount = 1000.0,
                category = "Otros",
                date = 1000L,
                isIncome = false
            )
        )

        dao.replaceAllTransactions(
            listOf(
                TransactionEntity(
                    title = "Nueva 1",
                    amount = 2000.0,
                    category = "Alimentos",
                    date = 2000L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Nueva 2",
                    amount = 3000.0,
                    category = "Trabajo",
                    date = 3000L,
                    isIncome = true
                )
            )
        )

        val result = dao.getAllTransactions().first()

        assertEquals(2, result.size)
        assertEquals("Nueva 2", result[0].title)
        assertEquals("Nueva 1", result[1].title)
    }

    @Test
    fun getTotalIncome_returnsOnlyIncomeSum() = runTest {
        dao.insertTransactions(
            listOf(
                TransactionEntity(
                    title = "Sueldo",
                    amount = 100000.0,
                    category = "Trabajo",
                    date = 1000L,
                    isIncome = true
                ),
                TransactionEntity(
                    title = "Freelance",
                    amount = 50000.0,
                    category = "Trabajo",
                    date = 2000L,
                    isIncome = true
                ),
                TransactionEntity(
                    title = "Comida",
                    amount = 10000.0,
                    category = "Alimentos",
                    date = 3000L,
                    isIncome = false
                )
            )
        )

        val result = dao.getTotalIncome().first()

        assertEquals(150000.0, result)
    }

    @Test
    fun getTotalExpenses_returnsOnlyExpenseSum() = runTest {
        dao.insertTransactions(
            listOf(
                TransactionEntity(
                    title = "Sueldo",
                    amount = 100000.0,
                    category = "Trabajo",
                    date = 1000L,
                    isIncome = true
                ),
                TransactionEntity(
                    title = "Comida",
                    amount = 10000.0,
                    category = "Alimentos",
                    date = 2000L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Internet",
                    amount = 5000.0,
                    category = "Servicios",
                    date = 3000L,
                    isIncome = false
                )
            )
        )

        val result = dao.getTotalExpenses().first()

        assertEquals(15000.0, result)
    }

    @Test
    fun getExpensesForCurrentMonth_returnsOnlyCurrentMonthExpenses() = runTest {
        dao.insertTransactions(
            listOf(
                TransactionEntity(
                    title = "Gasto anterior",
                    amount = 10000.0,
                    category = "Alimentos",
                    date = 500L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Gasto actual",
                    amount = 20000.0,
                    category = "Alimentos",
                    date = 1500L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Ingreso actual",
                    amount = 50000.0,
                    category = "Trabajo",
                    date = 2000L,
                    isIncome = true
                )
            )
        )

        val result = dao
            .getExpensesForCurrentMonth(1000L)
            .first()

        assertEquals(20000.0, result)
    }

    @Test
    fun getTransactionsForCurrentMonth_returnsOnlyTransactionsAfterStartDate() = runTest {
        dao.insertTransactions(
            listOf(
                TransactionEntity(
                    title = "Anterior",
                    amount = 1000.0,
                    category = "Otros",
                    date = 500L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Actual 1",
                    amount = 2000.0,
                    category = "Otros",
                    date = 1500L,
                    isIncome = false
                ),
                TransactionEntity(
                    title = "Actual 2",
                    amount = 3000.0,
                    category = "Trabajo",
                    date = 2000L,
                    isIncome = true
                )
            )
        )

        val result = dao
            .getTransactionsForCurrentMonth(1000L)
            .first()

        assertEquals(2, result.size)
        assertEquals("Actual 1", result[0].title)
        assertEquals("Actual 2", result[1].title)
    }
}