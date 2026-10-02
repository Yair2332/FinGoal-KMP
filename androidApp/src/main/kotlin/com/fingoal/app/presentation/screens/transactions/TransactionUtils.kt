package com.fingoal.app.presentation.screens.transactions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


// 1. Data Class de Categoria
data class Categoria(
    val nombre: String,
    val icono: ImageVector
)

// 2. Provider de categorías
object CategoryProvider {
    val categorias = listOf(
        Categoria("Alimentos", Icons.Default.Restaurant),
        Categoria("Transporte", Icons.Default.DirectionsCar),
        Categoria("Tecnología", Icons.Default.Computer),
        Categoria("Salud", Icons.Default.Favorite),
        Categoria("Entretenimiento", Icons.Default.Movie),
        Categoria("Otros", Icons.Default.ShoppingCart)
    )
}

// 3. Extensión para formatear la fecha
fun Long.toFormattedDate(): String {
    val instant = Instant.ofEpochMilli(this)
    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
    return instant.atZone(ZoneId.systemDefault()).format(formatter)
}

// 4. Función para obtener el icono por nombre de categoría
fun getIconForCategory(category: String): ImageVector {
    return CategoryProvider.categorias.find {
        it.nombre.equals(category, ignoreCase = true)
    }?.icono ?: Icons.Default.ShoppingCart
}