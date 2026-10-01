package com.fingoal.app.presentation.auth

object AuthValidator {

    private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

    fun validateLogin(email: String, pass: String): String? {
        return when {
            email.isBlank() || pass.isBlank() -> "Completa los campos"
            !email.matches(EMAIL_REGEX) -> "Correo inválido"
            pass.length <= 6 -> "Contraseña muy corta"
            else -> null // Retorna null si la validación es correcta
        }
    }

    fun validateRegister(email: String, pass: String, confirm: String): String? {
        return when {
            email.isBlank() || pass.isBlank() || confirm.isBlank() -> "Completa los campos"
            !email.matches(EMAIL_REGEX) -> "Correo inválido"
            pass.length <= 6 -> "Contraseña mínimo 6 caracteres"
            pass != confirm -> "Las contraseñas no coinciden"
            else -> null // Retorna null si la validación es correcta
        }
    }
}