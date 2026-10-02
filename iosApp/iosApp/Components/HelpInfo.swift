func getHelpInfoForRoute(_ route: String?) -> HelpInfo? {

    guard let route = route else {
        return nil
    }

    if route.contains("TransactionListRoute") {
        return HelpInfo(
            imageName: "transferencias",
            items: [
                InfoModalItem(
                    icon: "plus.circle",
                    title: "Registrar",
                    text: "Toca el botón '+' para añadir un nuevo movimiento."
                ),
                InfoModalItem(
                    icon: "pencil",
                    title: "Editar",
                    text: "Presiona una transacción para modificarla."
                ),
                InfoModalItem(
                    icon: "chart.bar",
                    title: "Balance",
                    text: "Visualiza el resumen mensual en la parte superior."
                )
            ]
        )
    }

    if route.contains("HabitListRoute") {
        return HelpInfo(
            imageName: "habitosinfo",
            items: [
                InfoModalItem(
                    icon: "plus",
                    title: "Crear Hábito",
                    text: "Define una rutina financiera recurrente."
                ),
                InfoModalItem(
                    icon: "pencil",
                    title: "Editar",
                    text: "Presiona un hábito para modificarlo."
                ),
                InfoModalItem(
                    icon: "flame",
                    title: "Racha",
                    text: "Completa hábitos varios días seguidos para mantener tu racha."
                )
            ]
        )
    }

    if route.contains("GoalListRoute") {
        return HelpInfo(
            imageName: "objetivosinfo",
            items: [
                InfoModalItem(
                    icon: "dollarsign.circle",
                    title: "Nueva Meta",
                    text: "Establece un objetivo de ahorro específico."
                ),
                InfoModalItem(
                    icon: "chart.line.uptrend.xyaxis",
                    title: "Progreso",
                    text: "Mira cuánto te falta para alcanzar tu meta."
                ),
                InfoModalItem(
                    icon: "pencil",
                    title: "Editar",
                    text: "Presiona una meta para modificarla."
                )
            ]
        )
    }

    return nil
}