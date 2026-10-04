package com.fingoal.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.usecase.transactions.AddTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.DeleteTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.GetTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.SyncTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.UpdateTransactionUseCase
import com.fingoal.app.ui.components.AssistantQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import com.fingoal.app.ui.screens.transactions.components.ExpenseCategorySummary
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val syncTransactionsUseCase: SyncTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TransactionUiState()
    )

    val uiState: StateFlow<TransactionUiState> =
        _uiState.asStateFlow()

    init {
        loadTransactions()
        syncWithServer()
    }

    /*
     * ============================================================
     * BOTTOM SHEET
     * ============================================================
     */

    fun showAddSheet() {
        _uiState.update {
            it.copy(
                showBottomSheet = true,
                editingTransaction = null
            )
        }
    }

    fun showEditSheet(
        transaction: Transaction
    ) {
        _uiState.update {
            it.copy(
                showBottomSheet = true,
                editingTransaction = transaction
            )
        }
    }

    fun hideSheet() {
        _uiState.update {
            it.copy(
                showBottomSheet = false,
                editingTransaction = null
            )
        }
    }

    /*
     * ============================================================
     * DELETE
     * ============================================================
     */

    fun showDeleteDialog(
        transaction: Transaction
    ) {
        _uiState.update {
            it.copy(
                transactionToDelete = transaction
            )
        }
    }

    fun hideDeleteDialog() {
        _uiState.update {
            it.copy(
                transactionToDelete = null
            )
        }
    }

    /*
     * ============================================================
     * CARGAR TRANSACCIONES
     * ============================================================
     */

    private fun loadTransactions() {

        _uiState.update {
            it.copy(
                isLoading = true
            )
        }

        viewModelScope.launch {

            getTransactionsUseCase()
                .catch { error ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
                .collect { list ->

                    /*
                     * Acá estaba el problema.
                     *
                     * Cada vez que llegan las transacciones,
                     * también construimos las preguntas del
                     * asistente.
                     */

                    val assistantQuestions =
                        buildAssistantQuestions(list)

                    _uiState.update {

                        it.copy(
                            isLoading = false,
                            transactions = list,
                            assistantQuestions = assistantQuestions
                        )
                    }
                }
        }
    }

    /*
     * ============================================================
     * SINCRONIZACIÓN
     * ============================================================
     */

    private fun syncWithServer() {

        viewModelScope.launch {
            syncTransactionsUseCase()
        }
    }

    /*
     * ============================================================
     * INSERTAR
     * ============================================================
     */

    fun insertTransaction(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            try {

                addTransactionUseCase(
                    title,
                    amount,
                    category,
                    isIncome,
                    description
                )

                hideSheet()

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    /*
     * ============================================================
     * ELIMINAR
     * ============================================================
     */

    fun deleteTransaction() {

        val transaction =
            _uiState.value.transactionToDelete
                ?: return

        viewModelScope.launch {

            try {

                deleteTransactionUseCase(
                    transaction.id,
                    transaction.remoteId
                )

                hideDeleteDialog()

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        errorMessage =
                            "Error al borrar: ${e.message}"
                    )
                }
            }
        }
    }

    /*
     * ============================================================
     * EDITAR
     * ============================================================
     */

    fun editTransaction(
        localId: Long,
        remoteId: String,
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            try {

                updateTransactionUseCase(
                    localId = localId,
                    remoteId = remoteId,
                    title = title,
                    amount = amount,
                    category = category,
                    isIncome = isIncome,
                    description = description
                )

                hideSheet()

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "Error al editar: ${e.message}"
                    )
                }
            }
        }
    }

    /*
     * ============================================================
     * PREGUNTAS DEL ASISTENTE
     * ============================================================
     */

    private fun buildAssistantQuestions(
        transactions: List<Transaction>
    ): List<AssistantQuestion> {

        val timeZone = TimeZone.currentSystemDefault()

        val now = Clock.System.now()
            .toLocalDateTime(timeZone)

        val today = now.date

        val startOfMonth = LocalDate(
            year = today.year,
            monthNumber = today.monthNumber,
            dayOfMonth = 1
        )

        val startOfMonthMillis = startOfMonth
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()

        val currentMonthTransactions = transactions.filter {
            it.date >= startOfMonthMillis
        }

        val ingresosMes = currentMonthTransactions
            .filter { it.isIncome }
            .sumOf { it.amount }

        val gastosMes = currentMonthTransactions
            .filter { !it.isIncome }
            .sumOf { it.amount }

        val disponibleMes = ingresosMes - gastosMes

        val cantidadTotal = transactions.size

        val cantidadIngresos = transactions.count { it.isIncome }

        val cantidadGastos = transactions.count { !it.isIncome }

        // ---------------------------------------------------------
        // DÍAS DEL MES
        // ---------------------------------------------------------

        val diasDelMes = when (today.monthNumber) {
            2 -> if (today.year % 4 == 0) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }

        val diasTranscurridos = today.dayOfMonth.coerceAtLeast(1)

        val diasRestantes = (
                diasDelMes - today.dayOfMonth + 1
                ).coerceAtLeast(1)

        // ---------------------------------------------------------
        // GASTO DIARIO
        // ---------------------------------------------------------

        val promedioGastoDiario =
            gastosMes / diasTranscurridos

        val gastoDiarioPermitido =
            disponibleMes / diasRestantes

        // ---------------------------------------------------------
        // PROYECCIÓN
        // ---------------------------------------------------------

        val gastoProyectado =
            promedioGastoDiario * diasDelMes

        // ---------------------------------------------------------
        // PORCENTAJE DE INGRESOS GASTADOS
        // ---------------------------------------------------------

        val porcentajeGastado =
            if (ingresosMes > 0) {
                (gastosMes / ingresosMes) * 100
            } else {
                0.0
            }

        // ---------------------------------------------------------
        // CATEGORÍA CON MAYOR GASTO
        // ---------------------------------------------------------

        val categoriaMayorGasto = currentMonthTransactions
            .filter { !it.isIncome }
            .groupBy { it.category }
            .mapValues { (_, items) ->
                items.sumOf { it.amount }
            }
            .maxByOrNull { it.value }

        // ---------------------------------------------------------
        // GASTO MÁS GRANDE
        // ---------------------------------------------------------

        val gastoMasGrande = currentMonthTransactions
            .filter { !it.isIncome }
            .maxByOrNull { it.amount }

        // ---------------------------------------------------------
        // CANTIDAD DE DÍAS CON GASTOS
        // ---------------------------------------------------------

        val diasConGastos = currentMonthTransactions
            .filter { !it.isIncome }
            .map { transaction ->
                transaction.date
            }
            .distinct()
            .size

        // ---------------------------------------------------------
        // RESPUESTAS
        // ---------------------------------------------------------

        return listOf(

            // =====================================================
            // RESUMEN
            // =====================================================

            AssistantQuestion(
                question = "¿Cuánto dinero tengo disponible?",
                answer = if (disponibleMes >= 0) {
                    "Después de tus gastos de este mes, " +
                            "tienes $${formatAmount(disponibleMes)} disponibles."
                } else {
                    "Este mes gastaste " +
                            "$${formatAmount(-disponibleMes)} más de lo que ingresaste."
                }
            ),

            AssistantQuestion(
                question = "¿Cuánto ingresé este mes?",
                answer =
                    "Este mes ingresaste " +
                            "$${formatAmount(ingresosMes)}."
            ),

            AssistantQuestion(
                question = "¿Cuánto gasté este mes?",
                answer =
                    "Este mes gastaste " +
                            "$${formatAmount(gastosMes)}."
            ),

            AssistantQuestion(
                question = "¿Cuántas transacciones tengo?",
                answer =
                    "Tienes $cantidadTotal transacciones registradas."
            ),

            // =====================================================
            // CONTROL DIARIO
            // =====================================================

            AssistantQuestion(
                question = "¿Cuánto puedo gastar por día?",
                answer = if (disponibleMes > 0) {
                    "Puedes gastar aproximadamente " +
                            "$${formatAmount(gastoDiarioPermitido)} por día " +
                            "durante los $diasRestantes días restantes."
                } else {
                    "Actualmente no tienes dinero disponible " +
                            "para nuevos gastos este mes."
                }
            ),

            AssistantQuestion(
                question = "¿Cuánto gasto por día?",
                answer =
                    "Tu promedio de gastos diarios este mes es " +
                            "$${formatAmount(promedioGastoDiario)}."
            ),

            AssistantQuestion(
                question = "¿Estoy gastando demasiado?",
                answer = when {
                    gastosMes == 0.0 ->
                        "Todavía no registraste gastos este mes."

                    disponibleMes <= 0 ->
                        "Sí. Tus gastos ya alcanzaron o superaron " +
                                "el dinero que ingresaste este mes."

                    promedioGastoDiario > gastoDiarioPermitido ->
                        "Sí. Tu ritmo actual de gastos está por encima " +
                                "del límite diario recomendado."

                    else ->
                        "No. Por ahora estás dentro de un ritmo de gasto " +
                                "compatible con tu dinero disponible."
                }
            ),

            // =====================================================
            // PROYECCIONES
            // =====================================================

            AssistantQuestion(
                question = "¿Cuánto voy a gastar al terminar el mes?",
                answer =
                    "Si mantienes tu ritmo actual, " +
                            "podrías terminar el mes gastando aproximadamente " +
                            "$${formatAmount(gastoProyectado)}."
            ),

            AssistantQuestion(
                question = "¿Voy a llegar con dinero a fin de mes?",
                answer = when {
                    disponibleMes <= 0 ->
                        "Si mantienes este ritmo, podrías llegar a fin de mes " +
                                "sin dinero disponible."

                    gastoProyectado < ingresosMes ->
                        "Sí. Manteniendo tu ritmo actual, " +
                                "podrías conservar aproximadamente " +
                                "$${formatAmount(ingresosMes - gastoProyectado)}."

                    else ->
                        "Tu ritmo actual indica que podrías gastar " +
                                "más de lo que ingresaste."
                }
            ),

            // =====================================================
            // INGRESOS
            // =====================================================

            AssistantQuestion(
                question = "¿Qué porcentaje de mis ingresos gasté?",
                answer =
                    if (ingresosMes > 0) {
                        "Has gastado aproximadamente " +
                                "${formatAmount(porcentajeGastado)}% " +
                                "de tus ingresos de este mes."
                    } else {
                        "Todavía no tienes ingresos registrados este mes."
                    }
            ),

            AssistantQuestion(
                question = "¿Cuánto dinero me queda de mis ingresos?",
                answer =
                    if (ingresosMes > 0) {
                        "De los $${formatAmount(ingresosMes)} " +
                                "que ingresaron este mes, " +
                                "te quedan $${formatAmount(disponibleMes)}."
                    } else {
                        "No tienes ingresos registrados este mes."
                    }
            ),

            // =====================================================
            // CATEGORÍAS
            // =====================================================

            AssistantQuestion(
                question = "¿En qué estoy gastando más?",
                answer = categoriaMayorGasto?.let {
                    "Tu categoría con mayor gasto este mes es " +
                            "\"${it.key}\", con " +
                            "$${formatAmount(it.value)}."
                } ?: "Todavía no tienes gastos registrados este mes."
            ),

            AssistantQuestion(
                question = "¿Cuál fue mi gasto más grande?",
                answer = gastoMasGrande?.let {
                    "\"${it.title}\" fue tu gasto más grande este mes, " +
                            "por $${formatAmount(it.amount)}."
                } ?: "Todavía no tienes gastos registrados este mes."
            ),

            // =====================================================
            // FRECUENCIA
            // =====================================================

            AssistantQuestion(
                question = "¿Cuántos días gasté este mes?",
                answer =
                    "Registraste gastos en $diasConGastos días " +
                            "diferentes durante este mes."
            ),

            AssistantQuestion(
                question = "¿Cuántos gastos hice este mes?",
                answer =
                    "Este mes registraste $cantidadGastos gastos."
            ),

            AssistantQuestion(
                question = "¿Cuántos ingresos tuve este mes?",
                answer =
                    "Este mes registraste $cantidadIngresos ingresos."
            ),

            // =====================================================
            // AHORRO
            // =====================================================

            AssistantQuestion(
                question = "¿Cuánto podría ahorrar este mes?",
                answer = if (disponibleMes > 0) {
                    "Si no realizas nuevos gastos innecesarios, " +
                            "podrías terminar el mes con " +
                            "$${formatAmount(disponibleMes)} disponibles para ahorrar."
                } else {
                    "Actualmente no tienes dinero disponible " +
                            "para destinar al ahorro."
                }
            ),

            AssistantQuestion(
                question = "¿Estoy ahorrando o gastando más de lo que ingreso?",
                answer = when {
                    disponibleMes > 0 ->
                        "Actualmente estás gastando menos de lo que ingresas. " +
                                "Tienes $${formatAmount(disponibleMes)} disponibles."

                    disponibleMes == 0.0 ->
                        "Actualmente estás gastando prácticamente todo " +
                                "lo que ingresas."

                    else ->
                        "Estás gastando más de lo que ingresas por " +
                                "$${formatAmount(-disponibleMes)}."
                }
            ),

            // =====================================================
            // RECOMENDACIÓN
            // =====================================================

            AssistantQuestion(
                question = "¿Cómo estoy manejando mi dinero?",
                answer = when {
                    gastosMes == 0.0 ->
                        "Todavía no hay suficientes gastos registrados " +
                                "para analizar tu comportamiento."

                    disponibleMes < 0 ->
                        "Tus gastos superan tus ingresos. " +
                                "Sería recomendable reducir gastos " +
                                "o aumentar tus ingresos."

                    porcentajeGastado >= 90 ->
                        "Has utilizado gran parte de tus ingresos. " +
                                "Conviene controlar los gastos restantes."

                    porcentajeGastado >= 70 ->
                        "Vas utilizando una parte importante de tus ingresos. " +
                                "Todavía tienes margen, pero conviene controlar " +
                                "los gastos."

                    else ->
                        "Tu situación parece saludable por ahora. " +
                                "Tus gastos están por debajo de tus ingresos."
                }
            )
        )
    }

    /*
     * ============================================================
     * FORMATO DE DINERO
     * ============================================================
     */

    private fun formatAmount(
        amount: Double
    ): String {
        return amount.toString()
    }

    private fun getCurrentMonthTransactions(): List<Transaction> {

        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val startOfMonth = LocalDate(
            year = now.year,
            monthNumber = now.monthNumber,
            dayOfMonth = 1
        )

        val startOfMonthMillis = startOfMonth
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

        return _uiState.value.transactions.filter {
            it.date >= startOfMonthMillis
        }
    }

    fun getExpenseCategorySummary(): List<ExpenseCategorySummary> {

        val expenses = getCurrentMonthTransactions()
            .filter { !it.isIncome }

        val totalExpenses = expenses.sumOf { it.amount }

        if (totalExpenses <= 0.0) {
            return emptyList()
        }

        return expenses
            .groupBy { it.category }
            .map { (category, transactions) ->
                val amount = transactions.sumOf { it.amount }

                ExpenseCategorySummary(
                    category = category,
                    amount = amount,
                    percentage = (amount / totalExpenses).toFloat()
                )
            }
            .sortedByDescending { it.amount }
    }

    fun getMonthlyIncome(): Double {
        return getCurrentMonthTransactions()
            .filter { it.isIncome }
            .sumOf { it.amount }
    }

    fun getMonthlyExpenses(): Double {
        return getCurrentMonthTransactions()
            .filter { !it.isIncome }
            .sumOf { it.amount }
    }

}

data class ExpenseCategorySummary(
    val category: String,
    val amount: Double,
    val percentage: Float
)