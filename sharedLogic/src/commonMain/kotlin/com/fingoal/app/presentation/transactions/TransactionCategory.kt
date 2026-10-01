package com.fingoal.app.presentation.transactions

data class TransactionCategory(
    val name: String,
    val iconKey: String
)

object CategoryProvider {
    val categories = listOf(
        TransactionCategory("Alimentos", "restaurant"),
        TransactionCategory("Transporte", "car"),
        TransactionCategory("Tecnología", "computer"),
        TransactionCategory("Salud", "heart"),
        TransactionCategory("Entretenimiento", "movie"),
        TransactionCategory("Otros", "cart")
    )
}