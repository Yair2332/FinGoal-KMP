package com.fingoal.app.ui.screens.transactions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant

data class Categoria(
    val nombre: String,
    val icono: ImageVector
)

object CategoryProvider {

    val categorias = listOf(
        Categoria("Alimentos", Icons.Default.Restaurant),
        Categoria("Transporte", Icons.Default.DirectionsCar),
        Categoria("Tecnología", Icons.Default.Computer),
        Categoria("Salud", Icons.Default.Favorite),
        Categoria("Entretenimiento", Icons.Default.Movie),
        Categoria("Compras", Icons.Default.ShoppingCart),
        Categoria("Hogar", Icons.Default.Home),
        Categoria("Educación", Icons.Default.School),
        Categoria("Viajes", Icons.Default.Flight),
        Categoria("Servicios", Icons.Default.Build),
        Categoria("Trabajo", Icons.Default.Work),
        Categoria("Otros", Icons.Default.MoreHoriz)
    )
}

fun Long.toFormattedDate(): String {
    val date = Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val month = when (date.monthNumber) {
        1 -> "enero"
        2 -> "febrero"
        3 -> "marzo"
        4 -> "abril"
        5 -> "mayo"
        6 -> "junio"
        7 -> "julio"
        8 -> "agosto"
        9 -> "septiembre"
        10 -> "octubre"
        11 -> "noviembre"
        12 -> "diciembre"
        else -> ""
    }

    return "${date.dayOfMonth} de $month"
}

fun getIconForCategory(category: String): ImageVector {
    return CategoryProvider.categorias.find {
        it.nombre.equals(category, ignoreCase = true)
    }?.icono ?: Icons.Default.ShoppingCart
}

fun Double.toAmountString(): String {
    val rounded = kotlin.math.round(this * 100) / 100
    val integerPart = rounded.toLong()
    val decimalPart = kotlin.math.abs(
        ((rounded - integerPart) * 100).toLong()
    )

    return "$integerPart.${decimalPart.toString().padStart(2, '0')}"
}